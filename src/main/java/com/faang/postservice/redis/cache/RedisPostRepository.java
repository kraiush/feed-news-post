package com.faang.postservice.redis.cache;

import com.faang.postservice.dto.cache.CommentCache;
import com.faang.postservice.dto.cache.PostCache;
import com.faang.postservice.exception.DataValidationException;
import com.faang.postservice.model.Comment;
import com.faang.postservice.service.comment.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

import static java.lang.Boolean.TRUE;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisPostRepository {

    @Value("${spring.data.redis.prefix.postKey}")
    private String postKey;
    @Value("${spring.data.redis.prefix.lockPostLikes}")
    private String lockPostLikesKey;
    @Value("${spring.data.redis.prefix.lockCommentLikes}")
    private String lockCommentLikeKey;
    @Value("${spring.data.redis.prefix.lockViews}")
    private String lockViewsKey;
    @Value("${spring.data.redis.prefix.lockComments}")
    private String lockCommentsKey;
    @Value("${spring.data.redis.commentsBoxSize}")
    private int commentsBoxSize;

    private final RedisTemplate<String, PostCache> cachePostTemplate;
    private final CommentServiceImpl commentService;
    private final RedissonClient redissonClient;
    private final RedisTransaction redisTransaction;
    private final RedisUserRepository redisUserRepository;

    public void save(PostCache postCache) {
        String key = postKey.formatted(postCache.getId());
        redisTransaction.futfill(cachePostTemplate, key, postCache);
    }

    public PostCache findById(long postId) {
        try {
            String key = postKey.formatted(postId);
            return cachePostTemplate.opsForValue().get(key);
        } catch (Exception ex) {
            String POST_IS_NOT_FOUND = "DataValidationException <!> Post with Id: %d not found in <PostCache>";
            throw new DataValidationException(String.format(POST_IS_NOT_FOUND, postId));
        }
    }

    public Optional<PostCache> findPostByKey(Long postId) {
        String key = postKey.formatted(postId);
        PostCache postCache = cachePostTemplate.opsForValue().get(key);
        return Optional.ofNullable(postCache);
    }

    public boolean hasPostById(long postId) {
        return TRUE.equals(cachePostTemplate.hasKey(postKey.formatted(postId)));
    }

    public List<PostCache> findPostsByIds(Collection<Long> postIds) {
        List<String> keys = postIds.stream()
                .map(id -> postKey.formatted(id))
                .toList();
        List<PostCache> postCaches = cachePostTemplate.opsForValue().multiGet(keys);
        return postCaches != null ? postCaches.stream()
                .filter(Objects::nonNull)
                .toList() : Collections.emptyList();
    }

    public void addComment(long postId, long commentId) {
        if (hasPostById(postId)) {
            RLock lock = redissonClient.getLock(lockCommentsKey.formatted(commentId));
            try {
                PostCache postCache = findById(postId);
                Comment comment = commentService.getById(commentId);
                CommentCache commentCache = CommentCache.builder()
                        .id(commentId)
                        .content(comment.getContent())
                        .authorId(comment.getAuthorId())
                        .postId(comment.getPost().getId())
                        .createdAt(comment.getCreatedAt())
                        .build();
                TreeSet<CommentCache> comments = postCache.getComments();
                while (comments.size() >= commentsBoxSize) {
                    comments.remove(comments.first());
                }
                comments.add(commentCache);
                postCache.setComments(comments);
                save(postCache);
                redisUserRepository.addLastPostId(postId, comment.getAuthorId());
                log.info("Comment added to postId: {} ", postId);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else log.info("Redis - addComment(): <PostCache> not found with postId: {} ", postId);
    }

    public void updateComment(long postId, long commentId) {
        if (hasPostById(postId)) {
            RLock lock = redissonClient.getLock(lockCommentsKey.formatted(commentId));
            try {
                PostCache postCache = findById(postId);
                Comment comment = commentService.getById(commentId);
                TreeSet<CommentCache> comments = postCache.getComments();
                for (CommentCache elem : comments) {
                    if (elem.getId().equals(commentId)) {
                        elem.setContent(comment.getContent());
                    }
                }
                postCache.setComments(comments);
                save(postCache);
                redisUserRepository.addLastPostId(postId, comment.getAuthorId());
                log.info("Comment of postId: {} updated", postId);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else log.info("Redis - updateComment(): <PostCache> not found with postId: {} ", postId);
    }

    public void likePost(long postId, long userId) {
        if (hasPostById(postId)) {
            RLock lock = redissonClient.getLock(lockPostLikesKey.formatted(postId));
            try {
                lock.lock();
                String key = postKey.formatted(postId);
                PostCache postCache = cachePostTemplate.opsForValue().get(key);
                assert postCache != null;
                postCache.incrementLikes();
                cachePostTemplate.opsForValue().set(key, postCache);
                redisUserRepository.addLastPostId(postId, userId);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else log.info("Redis - likePost: <PostCache> not found with postId: {} ", postId);
    }

    public void likeComment(long postId, long commentId, long userId) {
        if (hasPostById(postId)) {
            RLock lock = redissonClient.getLock(lockCommentLikeKey.formatted(postId));
            try {
                lock.lock();
                String key = postKey.formatted(postId);
                PostCache postCache = cachePostTemplate.opsForValue().get(key);
                assert postCache != null;
                TreeSet<CommentCache> comments = postCache.getComments();
                for (CommentCache elem : comments) {
                    if (elem.getId().equals(commentId)) {
                        elem.incrementLikes();
                    }
                }
                postCache.setComments(comments);
                cachePostTemplate.opsForValue().set(key, postCache);
                redisUserRepository.addLastPostId(postId, userId);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else log.info("Redis - likeComment: <PostCache> not found with postId: {} ", postId);
    }

    public void viewed(long postId, long userId) {
        if (hasPostById(postId)) {
            RLock lock = redissonClient.getLock(lockViewsKey.formatted(postId));
            try {
                lock.lock();
                String key = postKey.formatted(postId);
                PostCache postCache = cachePostTemplate.opsForValue().get(key);
                assert postCache != null;
                postCache.incrementViews();
                cachePostTemplate.opsForValue().set(key, postCache);
                redisUserRepository.addLastPostId(postId, userId);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else log.info("Redis - viewed: <PostCache> not found with postId: {} ", postId);
    }
}
