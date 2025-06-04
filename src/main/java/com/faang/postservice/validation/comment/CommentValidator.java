package com.faang.postservice.validation.comment;

import com.faang.postservice.model.Comment;
import com.faang.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
public class CommentValidator {

    private final CommentRepository commentRepository;

    public void existComment(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            String COMMENT_IS_NOT_FOUND = "<Comment> with Id: %d is not found";
            throw new EntityNotFoundException(String.format(COMMENT_IS_NOT_FOUND, commentId));
        }
    }

    public void checkAuthor(Comment comment, Long userId) {
        var authorId= comment.getAuthorId();
        if (!Objects.equals(userId, authorId))
        {
            String OPERATION_IS_NOT_AVAILABLE = "There is no <Comment> author Id: %d to OPERATE with <Comment> Id: %d";
            throw new IllegalArgumentException(String.format(OPERATION_IS_NOT_AVAILABLE, userId, comment.getId()));
        }
    }
}
