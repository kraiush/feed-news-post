package com.faang.postservice.service.post;

import com.faang.postservice.dto.cache.PostCache;
import com.faang.postservice.dto.cache.UserCache;
import com.faang.postservice.dto.event.PostEvent;
import com.faang.postservice.dto.event.ViewEvent;
import com.faang.postservice.dto.user.UserDto;
import com.faang.postservice.exception.ResourceNotFoundException;
import com.faang.postservice.kafka.AbstractEvent;
import com.faang.postservice.mapper.MapperUtil;
import com.faang.postservice.mapper.cache.PostCacheMapper;
import com.faang.postservice.mapper.dto.PostMapper;
import com.faang.postservice.model.Hashtag;
import com.faang.postservice.model.Post;
import com.faang.postservice.redis.cache.RedisNewsWireRepository;
import com.faang.postservice.redis.cache.RedisPostRepository;
import com.faang.postservice.redis.cache.RedisUserRepository;
import com.faang.postservice.repository.EventRepository;
import com.faang.postservice.repository.PostRepository;
import com.faang.postservice.service.corrector.ModerationDictionary;
import com.faang.postservice.service.hashtag.HashtagService;
import com.faang.postservice.service.outbox.EventType;
import com.faang.postservice.service.user.UserService;
import com.faang.postservice.validation.post.PostValidator;
import com.faang.postservice.validation.user.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class PostServiceImpl<T> extends AbstractEvent<T> implements PostService {

    @Value("${spring.data.hashtag-cache.size}")
    private int hashtagCacheSize;
    @Value("${scheduler.moderator.post.sublist-size}")
    private int sublistSize;

    private final Executor threadPoolForPostModeration;
    private final ModerationDictionary moderationDictionary;
    private final HashtagService hashtagService;
    private final PostCacheMapper postCacheMapper;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final RedisNewsWireRepository redisNewsWireRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final UserService userService;
    private final UserValidator userValidator;

    public PostServiceImpl(ObjectMapper objectMapper, EventRepository eventRepository,
                           Executor threadPoolForPostModeration, ModerationDictionary moderationDictionary,
                           HashtagService hashtagService, PostCacheMapper postCacheMapper, PostRepository postRepository,
                           PostValidator postValidator, RedisNewsWireRepository redisNewsWireRepository,
                           RedisPostRepository redisPostRepository, RedisUserRepository redisUserRepository,
                           UserService userService, UserValidator userValidator, PostMapper postMapper) {
        super(objectMapper, eventRepository);
        this.threadPoolForPostModeration = threadPoolForPostModeration;
        this.moderationDictionary = moderationDictionary;
        this.hashtagService = hashtagService;
        this.postCacheMapper = postCacheMapper;
        this.postMapper = postMapper;
        this.postRepository = postRepository;
        this.postValidator = postValidator;
        this.redisNewsWireRepository = redisNewsWireRepository;
        this.redisPostRepository = redisPostRepository;
        this.redisUserRepository = redisUserRepository;
        this.userValidator = userValidator;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> findAll() {
        final List<Post> posts = new ArrayList<>(postRepository.findAll());
        if (posts.isEmpty()) {
            log.info("List <Post> has no elements!");
            return null;
        }
        return MapperUtil.convertList(posts, Post.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Post getById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    return new EntityNotFoundException("<Post> with Id: " + postId + " has not been found");
                });
    }

    @Transactional
    public Post getPostById(Long postId, Long userId) {
        Post post = getById(postId);
        Set<Hashtag> hashTags = hashtagService.getHashtagsByPostId(postId);
        post.setHashtags(hashTags);
        ViewEvent event = new ViewEvent(postId, userId);
        saveEvent(EventType.VIEWED, event);
        log.info("<Post> with postId: {} has been viewed", postId);
        return post;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPublishedPostsByAuthorId(Long authorId) {
        log.info("User's published posts have taken from DB successfully, userId: {}", authorId);
        return postRepository.findPublishedPostsByAuthorId(authorId);
    }

    @Override
    @Transactional
    public Post create(Post entity, Set<String> hashtags) {
        long userId = entity.getAuthorId();
        userValidator.existUser(userId);
        if (hashtags != null) {
            Set<Hashtag> savedHashTags = saveHashTags(hashtags);
            entity.setHashtags(savedHashTags);
        }
        Post post = postRepository.save(entity);
        log.info("<Post> created: {} ", post);
        return post;
    }

    @Override
    @Transactional
    public Post update(Post entity, Set<String> hashtags) {
        long userId = entity.getAuthorId();
        userValidator.existUser(userId);
        postValidator.existPost(entity.getId());
        long postId = entity.getId();
        Post post = getById(postId);
        postValidator.checkAuthor(post, userId);
        post.setContent(entity.getContent());
        if (hashtags != null) {
            Set<Hashtag> updateHashTags = saveHashTags(hashtags);
            for (Hashtag hashtag : updateHashTags) {
                if (hashtagService.checkDoubling(postId, hashtag.getId()) == 0) {
                    hashtagService.insertEntry(postId, hashtag.getId());
                }
            }
        }
        if (post.isPublished()) {
            List<Long> followerIds = userService.getFollowerIds(userId);
            PostEvent event = new PostEvent(postId, userId, followerIds);
            saveEvent(EventType.UPDATED, event);
        }
        log.info("<Post> updated with postId: {}", postId);
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post publish(Long postId, Long userId) {
        userValidator.existUser(userId);
        postValidator.existPost(postId);
        Post entity = getById(postId);
        postValidator.checkAuthor(entity, userId);
        postValidator.isPublished(entity);
        entity.setPublished(true);
        entity.setPublishedAt(LocalDateTime.now());
        List<Long> followerIds = userService.getFollowerIds(userId);
        PostEvent event = new PostEvent(postId, userId, followerIds);
        saveEvent(EventType.PUBLISHED, event);
        return postRepository.save(entity);
    }

    protected Set<Hashtag> saveHashTags(Set<String> hashtags) {
        Set<Hashtag> savedHashTags = new HashSet<>();
        for (String name : hashtags) {
            Optional<Hashtag> hashtagOptional = hashtagService.getHashtagByName(name);
            if (hashtagOptional.isEmpty()) {
                Hashtag entity = new Hashtag();
                entity.setName(name);
                savedHashTags.add(hashtagService.save(entity));
            } else {
                savedHashTags.add(hashtagOptional.get());
            }
        }
        return savedHashTags;
    }

    @Transactional
    public void cachePost(Long postId) {
        Post post = getById(postId);
        post.setUpdatedAt(LocalDateTime.now());
        ;
        PostCache postCache = postCacheMapper.toPostCache(post);
        redisPostRepository.save(postCache);
    }

    @Transactional
    public void cacheUser(Long postId, Long userId) {
        UserDto userDto = userService.getUser(userId);
        if (postId != 0) {
            userDto.setLastPostId(postId);
        }
        UserCache userCache = MapperUtil.convertClass(userDto, UserCache.class);
        redisUserRepository.save(userCache);
    }

    @Transactional(readOnly = true)
    public Post getNotDeletedPostById(Long postId) {
        postValidator.existPost(postId);
        return postRepository.findByIdAndNotDeleted(postId).orElseThrow(
                () -> new ResourceNotFoundException("<Post> with Id has been deleted {} " + postId));
    }

    @Override
    @Transactional
    public void delete(Long postId, Long userId) {
        userValidator.existUser(userId);
        postValidator.existPost(postId);
        Post post = postRepository.findById(postId).get();
        postValidator.checkAuthor(post, userId);
        if (post.isDeleted()) {
            log.info("<Post> with postId: {} has already been deleted", postId);
        }
        post.setPublished(false);
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<Post> getPostsByHashtagName(String name) {
        List<Post> posts = new ArrayList<>();
        Set<Long> postIds = hashtagService.getPostIdsByHashtagName(name);
        for (Long postId : postIds) {
            Post post = getById(postId);
            Set<Hashtag> hashTags = hashtagService.getHashtagsByPostId(postId);
            post.setHashtags(hashTags);
            posts.add(post);
        }
        return posts;
    }

    @Transactional(readOnly = true)
    public List<Post> getHotPosts() {
        return postRepository.findHotPies();
    }

    public List<Post> getAllDraftsPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        log.info("User's drafts have taken from DB successfully, userId: {}", userId);
        return posts.stream()
                .filter(p -> !p.isPublished() && !p.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    public List<Post> getAllPublishedPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        log.info("User's published posts have taken from DB successfully, userId: {}", userId);
        return posts.stream()
                .filter(p -> p.isPublished() && !p.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
    }

    public void doPostModeration() {
        List<Post> notVerifiedPosts = postRepository.findNotVerified();
        List<List<Post>> partitionList = new ArrayList<>();

        if (notVerifiedPosts.size() > sublistSize) {
            partitionList = Lists.partition(notVerifiedPosts, sublistSize);
        } else {
            partitionList.add(notVerifiedPosts);
        }
        partitionList
                .forEach(p -> threadPoolForPostModeration.execute(() -> checkListForObsceneWords(p)));
        log.info("All posts have checked successfully");
    }

    private void checkListForObsceneWords(List<Post> list) {
        list.forEach(post -> {
            boolean checkResult = moderationDictionary.checkWordContent(post.getContent());
            log.info("Post with postId: {} has been checked for content obscene words", post.getId());
            post.setVerified(!checkResult);
            post.setVerifiedDate(LocalDateTime.now());
        });
        postRepository.saveAll(list);
    }
}
