package com.faang.postservice.repository;

import com.faang.postservice.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findByPostId(Long postId);

    @Query("select c from Comment c join fetch c.post where c.post.id=:id")
    Optional<Comment> findByPostIdWithJoinFetch(long id);

    @Query("SELECT c FROM Comment c WHERE c.verified IS NULL")
    List<Comment> findUnverifiedComments();
}


