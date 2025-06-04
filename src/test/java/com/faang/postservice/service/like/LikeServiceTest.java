package com.faang.postservice.service.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faang.postservice.dto.like.LikeDto;
import com.faang.postservice.model.Comment;
import com.faang.postservice.model.Like;
import com.faang.postservice.model.Post;
import com.faang.postservice.repository.EventRepository;
import com.faang.postservice.repository.LikeRepository;
import com.faang.postservice.service.comment.CommentServiceImpl;
import com.faang.postservice.service.post.PostServiceImpl;
import com.faang.postservice.validation.comment.CommentValidator;
import com.faang.postservice.validation.like.LikeValidator;
import com.faang.postservice.validation.post.PostValidator;
import com.faang.postservice.service.user.UserService;
import com.faang.postservice.validation.user.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeServiceImpl likeService;
    @Mock
    private CommentServiceImpl commentService;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeValidator likeValidator;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private PostServiceImpl postService;
    @Mock
    private PostValidator postValidator;
    @Mock
    private UserService userService;
    @Mock
    private UserValidator userValidator;

    private long postId;
    private long userId;
    private long commentId;
    private long likeId;
    private Post post;
    private Comment comment;
    private Like like;
    private Like likeComment;
    private LikeDto likeDtoComment;
    private LikeDto likeDtoPost;

    @BeforeEach
    public void setUp() {
        userId = ThreadLocalRandom.current().nextLong(1, 1000);
        postId = new Random().nextLong(1, 500);
        commentId = new Random().nextLong(51, 100);
        likeId = new Random().nextLong(1, 10);
        post = Post.builder().id(postId).authorId(new Random().nextLong(1, 100)).build();
        comment = Comment.builder().id(commentId).authorId(new Random().nextLong(1, 100)).post(post).build();
        like = Like.builder().id(likeId).userId(userId).post(post).createdAt(LocalDateTime.now()).build();
        likeComment = Like.builder().id(likeId).userId(userId).comment(comment).build();
        likeDtoPost = LikeDto.builder().id(likeId).userId(userId).postId(postId).build();
        likeDtoComment = LikeDto.builder().id(likeId).userId(userId).commentId(commentId).build();
    }

    @Test
    void givenNewLikeToPost_whenSave_thenSuccess() {
        doNothing().when(userValidator).existUser(userId);
        when(likeRepository.findByPostIdAndUserId(post.getId(), userId)).thenReturn(Optional.empty());
        when(postService.getNotDeletedPostById(postId)).thenReturn(post);
        when(likeRepository.save(any())).thenReturn(like);
        Like result = likeService.addLikeToPost(likeDtoPost);
        assertNotNull(result);
        verify(userValidator, times(1)).existUser(userId);
        verify(likeRepository, times(1)).findByPostIdAndUserId(post.getId(), userId);
        verify(postService, times(1)).getNotDeletedPostById(postId);
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void givenNewLikeToComment_whenSave_thenSuccess() {
        doNothing().when(userValidator).existUser(userId);
        when(commentService.getById(commentId)).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(comment.getId(), userId)).thenReturn(Optional.empty());
        when(likeRepository.save(any())).thenReturn(like);
        Like result = likeService.addLikeToComment(likeDtoComment);
        assertNotNull(result);
        verify(userValidator, times(1)).existUser(userId);
        verify(commentService, times(1)).getById(commentId);
        verify(likeRepository, times(1)).findByCommentIdAndUserId(comment.getId(), userId);
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void givenPostId_whenDelete_thenNothing() {
        doNothing().when(userValidator).existUser(userId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(like));
        doNothing().when(likeRepository).deleteById(likeId);
        likeService.deleteLikeFromPost(postId, userId);
        verify(userValidator, times(1)).existUser(userId);
        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
        verify(likeRepository, times(1)).deleteById(likeId);
    }

    @Test
    void givenCommentId_whenDelete_thenNothing() {
        doNothing().when(userValidator).existUser(userId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(likeComment));
        doNothing().when(likeRepository).deleteById(likeId);
        likeService.deleteLikeFromComment(commentId, userId);
        verify(userValidator, times(1)).existUser(userId);
        verify(likeRepository, times(1)).findByCommentIdAndUserId(commentId, userId);
        verify(likeRepository, times(1)).deleteById(likeId);
    }
}