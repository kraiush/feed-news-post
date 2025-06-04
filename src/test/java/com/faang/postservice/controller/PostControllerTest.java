package com.faang.postservice.controller;

import com.faang.postservice.config.context.UserContext;
import com.faang.postservice.dto.post.PostDto;
import com.faang.postservice.mapper.MapperUtil;
import com.faang.postservice.mapper.dto.PostMapper;
import com.faang.postservice.model.Post;
import com.faang.postservice.service.post.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    private MockMvc mockMvc;
    @InjectMocks
    private PostController controller;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostServiceImpl service;
    @Mock
    private UserContext userContext;

    private long userId;
    private long id1;
    private long id2;
    PostDto post1;
    PostDto post2;
    private Set<String> hashtagNames;
    List<PostDto> posts;

    @BeforeEach
    void setUp() {
        userId = ThreadLocalRandom.current().nextLong(1, 10000);
        id1 = new Random().nextLong(1, 500);
        id2 = new Random().nextLong(501, 1000);
        hashtagNames = Set.of("#hashtag_1", "#hashtag_2");
        post1 = PostDto.builder().id(id1).authorId(userId).content(randomAlphabetic(10)).hashtagNames(hashtagNames).build();
        post2 = PostDto.builder().id(id2).authorId(userId).content(randomAlphabetic(10)).build();
        posts = List.of(post1, post2);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void whenGetPostId_thenReturnPost() throws Exception {
        Post post = MapperUtil.convertClass(post1, Post.class);
        when(userContext.getUserId()).thenReturn(userId);
        when(service.getPostById(id1, userId)).thenReturn(post);
        mockMvc.perform(get("/api/v1/posts/{postId}", id1))
                .andExpect(status().isOk());
        verify(service, times(1)).getPostById(id1, userId);
    }

    @Test
    void givenNewPost_whenSave_thenSuccess() {
        Post post = MapperUtil.convertClass(post2, Post.class);
        when(service.create(post, hashtagNames)).thenReturn(post);
        Post value = service.create(post, hashtagNames);
        verify(service, times(1)).create(post, hashtagNames);
        assertNotNull(value);
    }

    @Test
    void givenPostObject_whenUpdate_thenReturnUpdatedPost() {
        post2.setContent(randomAlphabetic(100));
        Post post = MapperUtil.convertClass(post2, Post.class);
        when(service.update(post, hashtagNames)).thenReturn(post);
        Post value = service.update(post, hashtagNames);
        verify(service, times(1)).update(post, hashtagNames);
        assertNotNull(value);
    }

    @Test
    void givenPostId_whenDelete_thenNothing() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(service).delete(id2, userId);
        mockMvc.perform(delete("/api/v1/posts/{postId}", id2, userId));
        verify(service, times(1)).delete(id2, userId);
    }
}