package com.faang.postservice.service.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faang.postservice.dto.cache.PostCache;
import com.faang.postservice.dto.cache.UserCache;
import com.faang.postservice.dto.post.PostDto;
import com.faang.postservice.mapper.MapperUtil;
import com.faang.postservice.mapper.dto.PostMapper;
import com.faang.postservice.model.Hashtag;
import com.faang.postservice.model.Like;
import com.faang.postservice.model.Post;
import com.faang.postservice.redis.cache.RedisPostRepository;
import com.faang.postservice.redis.cache.RedisUserRepository;
import com.faang.postservice.repository.EventRepository;
import com.faang.postservice.repository.PostRepository;
import com.faang.postservice.service.hashtag.HashtagService;
import com.faang.postservice.service.user.UserService;
import com.faang.postservice.validation.post.PostValidator;
import com.faang.postservice.validation.user.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostServiceImpl postService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private HashtagService hashtagService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostValidator postValidator;
    @Mock
    private RedisPostRepository redisPostRepository;
    @Mock
    private RedisUserRepository redisUserRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserValidator userValidator;

    private Long userId;
    private Post post;
    private Post postAnother;
    private Set<Long> postIds;
    private List<Post> draftPosts;
    private List<Post> publishedPosts;
    private List<PostDto> draftPostDtos;
    private List<PostDto> publishedPostDtos;
    private List<Long> followerIds;
    private Set<String> hashtagNames;
    private Set<Hashtag> hashtags;
    private Set<Hashtag> updateHashTags;
    private long id1;
    private long id2;
    private String contentUpdated;

    @BeforeEach
    public void setUp() {
        userId = ThreadLocalRandom.current().nextLong(1, 10000);
        id1 = new Random().nextLong(1, 500);
        id2 = new Random().nextLong(501, 1000);
        postIds = Set.of(id1, id2);

        hashtagNames = Set.of("#hashtag_1", "#hashtag_2");
        hashtags = Set.of(Hashtag.builder().id(1L).name("#hashtag_1").build(), Hashtag.builder().id(2L).name("#hashtag_2").build());
        updateHashTags = Set.of(Hashtag.builder().id(3L).name("#hashtag_3").build(), Hashtag.builder().id(4L).name("#hashtag_4").build());

        post = Post.builder().id(id1).authorId(userId).content("Init content").hashtags(hashtags).build();
        postAnother = Post.builder().id(id2).authorId(userId).content("Another content").hashtags(hashtags).build();
        contentUpdated = "Updated content";
        followerIds = Arrays.asList(new Random().nextLong(1, 100), new Random().nextLong(101, 200));

        Post draftPost1 = Post.builder().id(1L).authorId(userId).content("Draft 1").published(true).publishedAt(LocalDateTime.now()).build();
        Post draftPost2 = Post.builder().id(2L).authorId(userId).content("Draft 2").published(true).publishedAt(LocalDateTime.now()).build();
        Post publishedPost1 = Post.builder().id(3L).authorId(userId).content("Published 1").published(true).publishedAt(LocalDateTime.now()).build();
        Post publishedPost2 = Post.builder().id(2L).authorId(userId).content("Published 2").published(true).publishedAt(LocalDateTime.now()).build();
        draftPosts = List.of(draftPost1, draftPost2);
        publishedPosts = List.of(publishedPost1, publishedPost2);
    }

    @Test
    void givenNewPost_whenSave_thenSuccess() {
        doNothing().when(userValidator).existUser(userId);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        Post result = postService.create(post, hashtagNames);
        verify(userValidator).existUser(userId);
        verify(userValidator, times(1)).existUser(userId);
        verify(postRepository, times(1)).save(any(Post.class));
        assertEquals(result, post);
    }

    @Test
    void givenPostObject_whenPublish_thenReturnPublishedPost() {
        doNothing().when(userValidator).existUser(userId);
        doNothing().when(postValidator).existPost(post.getId());
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        doNothing().when(postValidator).isPublished(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        Post result = postService.publish(post.getId(), post.getAuthorId());
        verify(userValidator, times(1)).existUser(userId);
        verify(postValidator, times(1)).existPost(post.getId());
        verify(postRepository, times(1)).findById(post.getId());
        verify(postValidator, times(1)).isPublished(post);
        verify(postRepository, times(1)).save(any(Post.class));
        assertTrue(result.isPublished());
        assertNotNull(post.getPublishedAt());
        assertEquals(post, result);
    }

    @Test
    void givenPostObject_whenUpdate_thenReturnUpdatedPost() {
        doNothing().when(userValidator).existUser(userId);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        post.setContent(contentUpdated);
        Post result = postService.update(post, null);
        verify(userValidator, times(1)).existUser(userId);
        verify(postRepository, times(1)).findById(post.getId());
        verify(postRepository, times(1)).save(any(Post.class));
        assertNotNull(result);
        assertEquals(post.getContent(), result.getContent());
    }

    @Test
    void givenPostId_whenDelete_thenNothing() {
        doNothing().when(postValidator).existPost(post.getId());
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        postService.delete(post.getId(), post.getAuthorId());
        verify(postValidator, times(1)).existPost(post.getId());
        verify(postRepository, times(1)).findById(post.getId());
        verify(postRepository, times(1)).save(any(Post.class));
        assertTrue(post.isDeleted());
        assertFalse(post.isPublished());
    }

    @Test
    void givenHashtagName__findPosts_thenReturnPosts() {
        String hashtagName = "#abracadbra";
        when(hashtagService.getPostIdsByHashtagName(hashtagName)).thenReturn(postIds);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(postRepository.findById(postAnother.getId())).thenReturn(Optional.of(postAnother));
        List<Post> result = postService.getPostsByHashtagName(hashtagName);
        verify(hashtagService, times(1)).getPostIdsByHashtagName(hashtagName);
        verify(postRepository, times(1)).findById(post.getId());
        verify(postRepository, times(1)).findById(postAnother.getId());
        assertEquals(result.size(), 2);
    }

    @Test
    void givenNotExistedPostId_whenDelete_thenNothing() {
        long id = new Random().nextLong(1001, 1500);
        when(postRepository.findById(99909L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> postService.delete(id, userId));
    }

    @Test
    void givenPostId_whenNotFound_thenEntityNotFoundException() {
        long id = new Random().nextLong(1001, 1500);
        when(postRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(id, userId));
    }

    @Test
    void givenPostId_whenFound_thenReturnPost() {
        when(postRepository.findById(id1)).thenReturn(Optional.of(post));
        postService.getPostById(id1, userId);
        verify(postRepository, times(1)).findById(post.getId());
    }

    @Test
    void givenPostId_getAllDraftsPosts_ByUserId() {
        when(postRepository.findByAuthorId(userId)).thenReturn(draftPosts);
        List<Post> value = postService.getAllDraftsPostsByUserId(userId);
        verify(postRepository, times(1)).findByAuthorId(userId);
        Assertions.assertNotNull(value);
    }

    @Test
    void givenUserId_getAllPublishedPostsByUser() {
        when(postRepository.findByAuthorId(userId)).thenReturn(publishedPosts);
        List<Post> value = postService.getAllPublishedPostsByUserId(userId);
        verify(postRepository, times(1)).findByAuthorId(userId);
        Assertions.assertNotNull(value);
    }

    @Test
    void givenNotExistedPostId_getException_NotInDataBase() {
        long postId = 1;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> postService.getById(postId));
    }

    @Test
    void getPostWhenValid() {
        long postId = 1l;
        Post post = new Post();
        post.setId(postId);
        post.setLikes(Arrays.asList(new Like(), new Like()));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Post result = postService.getPostById(postId, userId);
        assertDoesNotThrow(() -> postService.getPostById(postId, userId));
        assertEquals(post, result);
    }
}