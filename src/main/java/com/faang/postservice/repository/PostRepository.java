package com.faang.postservice.repository;

import com.faang.postservice.model.Post;
import feign.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.deleted = false")
    Optional<Post> findByIdAndNotDeleted(@Param("id") long id);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.id = :id")
    List<Post> findByIdWithLikes(long id);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(@Param("authorId") long authorId);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM post
            WHERE author_id IN (:authorId)
            AND created_at > now() - interval '7 days'
            AND published = true AND deleted = false
            """)
    List<Post> findPublishedPostsByAuthorId(@Param("authorId") long authorId);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM post
            WHERE created_at > now() - interval '7 days'
            AND published = true AND deleted = false
            """)
    List<Post> findHotPies();

    @Query("SELECT p FROM Post p WHERE p.verifiedDate = null OR p.updatedAt >= p.verifiedDate")
    List<Post> findNotVerified();

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.corrected = false")
    List<Post> findNotPublished();

    @Query("SELECT p FROM Post p WHERE p.id IN :postIds")
    List<Post> findPostsByIds(List<Long> postIds);

     @Query("SELECT p FROM Post p WHERE p.authorId IN :postAuthorIds " +
            "AND p.createdAt > (SELECT sub.createdAt FROM Post sub WHERE sub.id = :lastPostId) " +
            "ORDER BY p.createdAt DESC")
    List<Post> findPostsByAuthorIdsAndLastPostId(
            @Param("postAuthorIds") List<Long> postAuthorIds,
            @Param("lastPostId") Long lastPostId,
            Pageable pageable);
}
