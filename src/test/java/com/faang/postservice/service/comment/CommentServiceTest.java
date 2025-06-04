package com.faang.postservice.service.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faang.postservice.config.context.UserContext;
import com.faang.postservice.dto.cache.UserCache;
import com.faang.postservice.dto.comment.CommentDto;
import com.faang.postservice.mapper.MapperUtil;
import com.faang.postservice.model.Comment;
import com.faang.postservice.model.Post;
import com.faang.postservice.redis.cache.RedisUserRepository;
import com.faang.postservice.repository.CommentRepository;
import com.faang.postservice.repository.EventRepository;
import com.faang.postservice.repository.PostRepository;
import com.faang.postservice.service.user.UserService;
import com.faang.postservice.validation.comment.CommentValidator;
import com.faang.postservice.validation.post.PostValidator;
import com.faang.postservice.validation.user.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentServiceImpl service;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostValidator postValidator;
    @Mock
    private RedisUserRepository redisUserRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private UserService userService;
    @Mock
    private UserValidator userValidator;

    private long userId;
    private long postId;
    private long commentId;
    private Comment comment;
    private CommentDto commentDto;
    private CommentDto updatedCommentDto;

    @BeforeEach
    void setUp() {
        userId = ThreadLocalRandom.current().nextLong(1, 10000);
        postId = new Random().nextLong(1, 500);
        commentId = new Random().nextLong(101, 200);
        commentDto = CommentDto.builder().id(commentId).authorId(userId).postId(postId).content("Init content").build();
        updatedCommentDto = CommentDto.builder().id(commentId).authorId(userId).postId(postId).content("Updated content").build();
        comment = MapperUtil.convertClass(commentDto, Comment.class);
    }

    @Test
    void givenNewComment_whenSave_thenSuccess() {
        doNothing().when(userValidator).existUser(userId);
        when(commentRepository.save(comment)).thenReturn(comment);
        Comment result = service.create(comment);
        verify(userValidator).existUser(userId);
        verify(userValidator, times(1)).existUser(userId);
        verify(commentRepository).save(comment);
        assertNotNull(result);
        assertEquals(comment, result);
    }

    @Test
    void whenGetCommentId_thenReturnAllComments() {
        when(commentRepository.findByPostId(postId)).thenReturn(List.of(comment));
        List value = service.getAllByPostId(postId);
        verify(commentRepository).findByPostId(postId);
        assertNotNull(value);
    }

    @Test
    void givenCommentObject_whenUpdate_thenReturnUpdatedComment() {
        doNothing().when(userValidator).existUser(userId);
        doNothing().when(commentValidator).existComment(commentId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        Comment updatedComment = MapperUtil.convertClass(updatedCommentDto, Comment.class);
        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);
        Comment result = service.update(updatedComment, userId);
        verify(userValidator, times(1)).existUser(userId);
        verify(commentValidator, times(1)).existComment(commentId);
        verify(commentRepository, times(1)).findById(commentId);
        assertNotNull(result);
        assertEquals(result.getContent(), updatedComment.getContent());
    }

    @Test
    void givenCommentId_whenGetRecord_thenReturnException() {
        long commentId = 1;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getById(commentId));
    }

    @Test
    void givenCommentId_GetById_thenSuccess() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        Comment result = service.getById(commentId);
        assertDoesNotThrow(() -> service.getById(commentId));
        assertEquals(comment, result);
    }

    @Test
    void givenCommentId_whenDelete_thenNothing() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        service.delete(commentId, userId);
        verify(commentRepository).findById(commentId);
    }
}