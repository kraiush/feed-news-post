package com.faang.postservice.service.comment;

import com.faang.postservice.model.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> getAllByPostId(Long id);

    Comment getById(Long commentId);

    Comment create(Comment comment);

    Comment update(Comment comment, Long userId);

    void delete(Long commentId, Long userId);

    void deleteAllByPostId(Long postId);

    List<Comment> getUnverifiedComments();

    void saveAll(List<Comment> comments);
}
