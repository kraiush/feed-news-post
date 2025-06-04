package com.faang.postservice.service.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faang.postservice.dto.event.LikeEvent;
import com.faang.postservice.dto.like.LikeDto;
import com.faang.postservice.kafka.AbstractEvent;
import com.faang.postservice.model.Comment;
import com.faang.postservice.model.Like;
import com.faang.postservice.model.Post;
import com.faang.postservice.repository.EventRepository;
import com.faang.postservice.repository.LikeRepository;
import com.faang.postservice.service.comment.CommentServiceImpl;
import com.faang.postservice.service.post.PostServiceImpl;
import com.faang.postservice.validation.user.UserValidator;
import com.faang.postservice.service.outbox.EventType;
import com.faang.postservice.validation.comment.CommentValidator;
import com.faang.postservice.validation.like.LikeValidator;
import com.faang.postservice.validation.post.PostValidator;
import com.faang.postservice.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LikeServiceImpl<T> extends AbstractEvent<T> implements LikeService {

    private final CommentServiceImpl commentService;
    private final CommentValidator commentValidator;
    private final LikeRepository likeRepository;
    private final LikeValidator likeValidator;
    private final PostServiceImpl postService;
    private final PostValidator postValidator;
    private final UserService userService;
    private final UserValidator userValidator;

    public LikeServiceImpl(ObjectMapper objectMapper, EventRepository eventRepository,
                           CommentValidator commentValidator, CommentServiceImpl commentService,
                           LikeRepository likeRepository, LikeValidator likeValidator, PostServiceImpl postService,
                           PostValidator postValidator, UserService userService, UserValidator userValidator) {
        super(objectMapper, eventRepository);
        this.commentValidator = commentValidator;
        this.commentService = commentService;
        this.likeRepository = likeRepository;
        this.likeValidator = likeValidator;
        this.postService = postService;
        this.postValidator = postValidator;
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @Override
    public List<Like> getLikesByPostId(Long postId) {
        return likeRepository.findByPostId(postId).stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .toList();
    }

    @Override
    public List<Like> getLikesByCommentId(Long commentId) {
        return likeRepository.findByCommentId(commentId).stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .toList();
    }

    public List<Long> getUsersLikedPost(Long postId) {
        return likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();
    }

    @Override
    @Transactional
    public Like addLikeToPost(LikeDto like) {
        long postId = like.getPostId();
        long userId = like.getUserId();
        userValidator.existUser(userId);
        postValidator.existPost(postId);
        Optional<Like> optionalLike = likeRepository.findByPostIdAndUserId(postId, userId);
        likeValidator.checkDuplicateLike(optionalLike);
        Post post = postService.getNotDeletedPostById(postId);
        Like entity = Like.builder()
                .userId(userId)
                .post(post)
                .build();
        LikeEvent event = new LikeEvent(postId, 0, userId);
        saveEvent(EventType.LIKED_POST, event);
        log.info("Added Like to Post - postId: {} ", postId);
        return likeRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteLikeFromPost(Long postId, Long userId) {
        userValidator.existUser(userId);
        Optional<Like> like = likeRepository.findByPostIdAndUserId(postId, userId);
        likeValidator.checkAvailabilityLike(like);
        likeValidator.checkAuthor(like.get(), userId);
        likeRepository.deleteById(like.get().getId());
    }

    @Override
    public Like addLikeToComment(LikeDto like) {
        long userId = like.getUserId();
        long commentId = like.getCommentId();
        userValidator.existUser(userId);
        commentValidator.existComment(commentId);
        Comment comment = commentService.getById(commentId);
        Long postId = comment.getPost().getId();
        Optional<Like> optionalLike = likeRepository.findByCommentIdAndUserId(commentId, userId);
        likeValidator.checkDuplicateLike(optionalLike);
        Like entity = Like.builder()
                .userId(userId)
                .comment(comment)
                .build();
        LikeEvent event = new LikeEvent(postId, commentId, userId);
        saveEvent(EventType.LIKED_COMMENT, event);
        log.info("Added Like to Comment - commentId: {} ", commentId);
        return likeRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteLikeFromComment(Long commentId, Long userId) {
        userValidator.existUser(userId);
        Optional<Like> like = likeRepository.findByCommentIdAndUserId(commentId, userId);
        likeValidator.checkAvailabilityLike(like);
        likeValidator.checkAuthor(like.get(), userId);
        likeRepository.deleteById(like.get().getId());
    }
}
