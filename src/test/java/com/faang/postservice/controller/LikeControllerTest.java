package com.faang.postservice.controller;

import com.faang.postservice.config.context.UserContext;
import com.faang.postservice.dto.like.LikeDto;
import com.faang.postservice.model.Like;
import com.faang.postservice.service.like.LikeServiceImpl;
import com.faang.postservice.mapper.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    @InjectMocks
    private LikeController likeController;
    @Mock
    private LikeServiceImpl likeService;
    @Mock
    private UserContext userContext;

    private long userId;
    private long postId;
    private long commentId;
    private LikeDto likeDto;

    @BeforeEach
    void setUp() {
        postId = new Random().nextLong(1, 500);
        userId = new Random().nextLong(1, 100);
        commentId = new Random().nextLong(51, 100);
    }

    @Test
    void givenNewLikeToPost_whenSave_thenSuccess() {
        likeDto = LikeDto.builder().id(new Random().nextLong(1, 10)).userId(userId).postId(postId).build();
        Like like = MapperUtil.convertClass(likeDto, Like.class);
        when(userContext.getUserId()).thenReturn(userId);
        when(likeService.addLikeToPost(likeDto)).thenReturn(like);
        likeController.addLikeToPost(likeDto);
        verify(likeService, times(1)).addLikeToPost(likeDto);
    }

    @Test
    void givenPostIdAndUserId_whenDeleteLike_thenNothing() {
        when(userContext.getUserId()).thenReturn(userId);
        likeController.deleteLikeFromPost(postId);
        verify(userContext, times(1)).getUserId();
        verify(likeService, times(1)).deleteLikeFromPost(postId, userId);
    }

    @Test
    void givenNewLikeToComment_whenSave_thenSuccess() {
        LikeDto likeDto = LikeDto.builder().id(new Random().nextLong(1, 10)).commentId(commentId).userId(userId).build();
        Like like = MapperUtil.convertClass(likeDto, Like.class);
        when(userContext.getUserId()).thenReturn(userId);
        when(likeService.addLikeToComment(likeDto)).thenReturn(like);
        likeController.addLikeToComment(likeDto);
        verify(likeService, times(1)).addLikeToComment(likeDto);
    }

    @Test
    void givenCommentIdAndUserId_whenDelete_thenNothing() {
        when(userContext.getUserId()).thenReturn(userId);
        likeController.deleteLikeFromComment(commentId);
        verify(userContext, times(1)).getUserId();
        verify(likeService).deleteLikeFromComment(commentId, userId);
    }
}