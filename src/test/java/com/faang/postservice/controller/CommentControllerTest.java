package com.faang.postservice.controller;

import com.faang.postservice.config.context.UserContext;
import com.faang.postservice.dto.comment.CommentDto;
import com.faang.postservice.model.Comment;
import com.faang.postservice.service.comment.CommentServiceImpl;
import com.faang.postservice.mapper.MapperUtil;
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
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;
    @InjectMocks
    private CommentController controller;
    @Mock
    private CommentServiceImpl service;
    @Mock
    private UserContext userContext;

    CommentDto comment1;
    CommentDto comment2;
    List<CommentDto> comments;
    private long userId;
    private long postId;
    private long id1;
    private long id2;

    @BeforeEach
    void setUp() {
        userId = ThreadLocalRandom.current().nextLong(1, 1000);
        id1 = new Random().nextLong(1, 500);
        id2 = new Random().nextLong(501, 1000);
        postId = new Random().nextLong(1, 1000);
        comment1 = CommentDto.builder().id(id1).postId(postId).content(randomAlphabetic(50)).authorId(userId).build();
        comment2 = CommentDto.builder().id(id2).postId(postId).content(randomAlphabetic(50)).authorId(userId).build();
        comments = List.of(comment1, comment2);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void givenNewComment_whenSave_thenSuccess() {
        Comment comment = MapperUtil.convertClass(comment1, Comment.class);
        when(userContext.getUserId()).thenReturn(userId);
        when(service.create(comment)).thenReturn(comment);
        controller.createComment(comment1);
        verify(service, times(1)).create(comment);
    }

    @Test
    void whenGetAllCommentsByPostId_thenReturnCommentsList() throws Exception {
        when(service.getAllByPostId(postId)).thenReturn(comments);
        mockMvc.perform(get("/api/v1/comments/post/{postId}", postId))
                .andExpect(status().isOk());
        verify(service, times(1)).getAllByPostId(postId);
    }

    @Test
    void givenCommentObject_whenUpdate_thenReturnUpdatedComment() {
        comment1.setContent(randomAlphabetic(100));
        Comment comment = MapperUtil.convertClass(comment1, Comment.class);
        when(userContext.getUserId()).thenReturn(userId);
        when(service.update(comment, userId)).thenReturn(comment);
        controller.updateComment(comment1);
        verify(service, times(1)).update(comment, userId);
    }

    @Test
    void givenCommentId_whenDelete_thenNothing() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(service).delete(id2, userId);
        mockMvc.perform(delete("/api/v1/comments/{commentId}", id2));
        verify(service, times(1)).delete(id2, userId);
    }
}

