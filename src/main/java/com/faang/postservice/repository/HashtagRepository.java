package com.faang.postservice.repository;

import com.faang.postservice.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    @Query(value = "SELECT * FROM hashtag WHERE name= :name", nativeQuery = true)
    Optional<Hashtag> findHashtagByName(String name);

    @Query(value = "SELECT * FROM hashtag h " +
            "JOIN post_hashtags ON (h.id = post_hashtags.hashtag_id) " +
            "WHERE post_hashtags.post_id= :postId",
            nativeQuery = true)
    Set<Hashtag> findHashtagsByPostId(@Param("postId") long post_id);


    @Query(value = "SELECT ph.post_id FROM post_hashtags ph " +
            "JOIN hashtag h ON (h.id = ph.hashtag_id) " +
            "WHERE h.name=:name",
            nativeQuery = true)
    Set<Long> findPostIdsByHashtagName(String name);

    @Query(value = "SELECT COUNT(1) FROM post_hashtags WHERE post_id = :postId AND hashtag_id = :hashtagId",
            nativeQuery = true)
    int checkDoubleEntry(@Param("postId") long post_id, @Param("hashtagId") long hashtag_id);

    @Modifying
    @Query(value = "INSERT INTO post_hashtags(post_id, hashtag_id) VALUES (:postId, :hashtagId)",
            nativeQuery = true)
    void insertEntry(@Param("postId") long post_id, @Param("hashtagId") long hashtag_id);

    @Modifying
    @Query(value = "DELETE FROM post_hashtags WHERE post_id = :postId AND hashtag_id = :hashtagId",
            nativeQuery = true)
    void deleteHashTagFromPost(@Param("postId") long post_id, @Param("hashtagId") long hashtag_id);

    @Modifying
    @Query(value = "DELETE FROM post_hashtags WHERE post_id = :postId",
            nativeQuery = true)
    void deleteHashTagsFromPost(@Param("postId") long post_id);

    @Query(value = "SELECT p.* FROM post p " +
            "JOIN post_hashtags ph ON (p.id = ph.post_id) " +
            "JOIN hashtag h ON (h.id = ph.hashtag_id) " +
            "WHERE h.name=:name",
            nativeQuery = true)
    Set<Object> findPostsByHashtagName(String name);
}




