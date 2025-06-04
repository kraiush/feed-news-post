package com.faang.postservice.service;

import com.faang.postservice.dto.cache.PostCache;
import com.faang.postservice.dto.cache.UserCache;
import com.faang.postservice.dto.user.UserDto;
import com.faang.postservice.mapper.MapperUtil;
import com.faang.postservice.mapper.cache.PostCacheMapper;
import com.faang.postservice.model.Post;
import com.faang.postservice.redis.cache.RedisNewsWireRepository;
import com.faang.postservice.redis.cache.RedisPostRepository;
import com.faang.postservice.redis.cache.RedisUserRepository;
import com.faang.postservice.service.post.PostServiceImpl;
import com.faang.postservice.service.user.UserService;
import com.faang.postservice.validation.post.PostValidator;
import com.faang.postservice.validation.user.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedNewsService {

    private final PostCacheMapper postCacheMapper;
    private final PostServiceImpl postService;
    private final PostValidator postValidator;
    private final RedisNewsWireRepository redisNewsWireRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final UserService userService;
    private final UserValidator userValidator;

    public List<PostCache> gePersonalNews(long userId) {
        userValidator.existUser(userId);
        if (cacheFeedNewsIsEmpty()) {
            heatFeedNews(userId);
            List<Long> userIds = userService.getSubscriberIds(userId);
            List<Post> postsJoin = new ArrayList<>();
            for (Long authorId : userIds) {
                List<Post> posts = postService.getPublishedPostsByAuthorId(authorId);
                postsJoin = Stream.concat(postsJoin.stream(), posts.stream()).toList();
            }
            if (!postsJoin.isEmpty()) {
                return postCacheMapper.toListPostCache(postsJoin);
            }
            return null;
        } else {
            Long lastPostId;
            if (redisUserRepository.hasUserById(userId)) {
                lastPostId = redisUserRepository.findById(userId).getLastPostId();
            } else {
                lastPostId = 0L;
            }
            Set<Long> postIds = redisNewsWireRepository.getCachedRecords(userId, lastPostId);
            if (!postIds.isEmpty()) {
                return getCachedPosts(postIds.stream()
                        .filter(e -> e >= lastPostId)
                        .collect(Collectors.toSet()));
            }
        }
        return null;
    }

    @Transactional
    public List<PostCache> heatFeedNews(long userId) {
        userValidator.existUser(userId);
        List<Post> posts = postService.getHotPosts();
        for (Post post : posts) {
            long postId = post.getId();
            long authorId = post.getAuthorId();
            cacheFeedNewsForUser(postId, authorId);
            cachePost(post);
            UserDto user = userService.getUser(authorId);
            cacheUser(user);
            List<Long> userIds = userService.getFollowerIds(authorId);
            cacheFeedNewsByUserIds(postId, userIds);
        }
        return postCacheMapper.toListPostCache(posts);
    }

    public void cacheFeedNewsForUser(Long postId, Long userId) {
        postValidator.existPost(postId);
        LocalDateTime timePublication = postService.getById(postId).getPublishedAt();
        ZonedDateTime zdt = ZonedDateTime.of(timePublication, ZoneId.systemDefault());
        double datetime = zdt.toInstant().toEpochMilli();
        redisNewsWireRepository.addPostToUser(postId, userId, datetime);
    }

    @Async
    public void cacheFeedNewsByUserIds(long postId, List<Long> userIds) {
        postValidator.existPost(postId);
        LocalDateTime timePublication = postService.getById(postId).getPublishedAt();
        ZonedDateTime zdt = ZonedDateTime.of(timePublication, ZoneId.systemDefault());
        double datetime = zdt.toInstant().toEpochMilli();
        redisNewsWireRepository.addPostToFollowers(postId, userIds, datetime);
    }

    private List<PostCache> getCachedPosts(Set<Long> postIds) {
        return postIds.stream()
                .map(key -> redisPostRepository.findPostByKey(key).orElseGet(() ->
                        getPostInDbAndSaveToCache(key)))
                .toList();
    }

    private PostCache getPostInDbAndSaveToCache(Long postId) {
        Post post = postService.getById(postId);
        cachePost(post);
        return postCacheMapper.toPostCache(post);
    }

    private void cachePost(Post post) {
        PostCache postCache = postCacheMapper.toPostCache(post);
        redisPostRepository.save(postCache);
    }

    private void cacheUser(UserDto userDto) {
        UserCache userCache = MapperUtil.convertClass(userDto, UserCache.class);
        redisUserRepository.save(userCache);
    }

    public Boolean cacheFeedNewsIsEmpty() {
        return redisNewsWireRepository.cacheIsEmpty();
    }

    public void addCommentToPost(long postId, long commentId) {
        redisPostRepository.addComment(postId, commentId);
    }

    public void updateCommentToPost(long postId, long commentId) {
        redisPostRepository.updateComment(postId, commentId);
    }

    public void viewPost(long postId, long authorId) {
        redisPostRepository.viewed(postId, authorId);
    }

    public void addLikeToPost(long postId, long authorId) {
        redisPostRepository.likePost(postId, authorId);
    }

    public void addLikeToComment(long postId, long commentId, long authorId) {
        redisPostRepository.likeComment(postId, commentId, authorId);
    }

    public static <T> List<T> concatenateLists(List<T>... lists) {
        return Arrays.stream(lists).flatMap(Collection::stream).collect(Collectors.toList());
    }
}

