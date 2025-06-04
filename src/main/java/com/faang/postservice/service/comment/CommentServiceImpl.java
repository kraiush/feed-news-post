package com.faang.postservice.service.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faang.postservice.dto.event.CommentEvent;
import com.faang.postservice.kafka.AbstractEvent;
import com.faang.postservice.model.Comment;
import com.faang.postservice.redis.cache.RedisUserRepository;
import com.faang.postservice.repository.CommentRepository;
import com.faang.postservice.repository.EventRepository;
import com.faang.postservice.service.user.UserService;
import com.faang.postservice.validation.comment.CommentValidator;
import com.faang.postservice.service.outbox.EventType;
import com.faang.postservice.validation.post.PostValidator;
import com.faang.postservice.validation.user.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl<T> extends AbstractEvent<T> implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final PostValidator postValidator;
    private final RedisUserRepository redisUserRepository;
    private final UserService userService;
    private final UserValidator userValidator;

    public CommentServiceImpl(ObjectMapper objectMapper, EventRepository eventRepository, CommentRepository commentRepository,
                              CommentValidator commentValidator, PostValidator postValidator, RedisUserRepository redisUserRepository,
                              UserService userService, UserValidator userValidator) {
        super(objectMapper, eventRepository);
        this.commentRepository = commentRepository;
        this.commentValidator = commentValidator;
        this.postValidator = postValidator;
        this.redisUserRepository = redisUserRepository;
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllByPostId(Long postId) {
        postValidator.existPost(postId);
        List<Comment> comments = commentRepository.findByPostId(postId);
        if (comments.isEmpty()) {
            String msg = "Post with Id: %d has no comments";
            log.error(String.format(msg, postId));
        }
        return comments.stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("<Comment> with Id <" + commentId + "> is not found");
                    return new EntityNotFoundException("<Comment> Id " + commentId + " is not found");
                });
    }

    @Override
    @Transactional
    public Comment create(Comment entity) {
        var postId = entity.getPost().getId();
        var authorId = entity.getAuthorId();
        userValidator.existUser(authorId);
        postValidator.existPost(postId);
        Comment comment = commentRepository.save(entity);
        CommentEvent event = new CommentEvent(postId, comment.getId());
        saveEvent(EventType.COMMENTED, event);
        log.info("Comment added to postId: {} authored by: {}", postId, authorId);
        return comment;
    }

    @Override
    @Transactional
    public Comment update(Comment entity, Long userId) {
        userValidator.existUser(userId);
        var commentId = entity.getId();
        commentValidator.existComment(commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        commentValidator.checkAuthor(comment, userId);
        var postId = comment.getPost().getId();
        comment.setContent(entity.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        CommentEvent event = new CommentEvent(postId, commentId);
        saveEvent(EventType.COMMENT_UPDATED, event);
        log.info("Comment updated to postId: {} authored by: {}", postId, userId);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void delete(Long commentId, Long userId) {
        var comment = getById(commentId);
        commentValidator.checkAuthor(comment, userId);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void deleteAllByPostId(Long postId) {
        postValidator.existPost(postId);
        List<Comment> comments = commentRepository.findByPostId(postId);
        if (comments.isEmpty()) {
            log.info("No <Comment> elements found to delete");
        } else
            commentRepository.deleteAll(comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getUnverifiedComments() {
        return commentRepository.findUnverifiedComments();
    }

    @Override
    @Transactional
    public void saveAll(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }
}
