package com.faang.postservice.service.post;

import com.faang.postservice.model.Post;

import java.util.List;
import java.util.Set;

public interface PostService {

    List<Post> findAll();

    Post getById(Long id);

    Post getPostById(Long postId, Long userId);

    Post create(Post post, Set<String> hashtags);

    Post publish(Long id, Long userId);

    Post update(Post post, Set<String> hashtags);

    List<Post> getAllDraftsPostsByUserId(Long userId);

    List<Post> getPublishedPostsByAuthorId(Long authorId);

    List<Post> getAllPublishedPostsByUserId(Long userId);

    List<Post> getPostsByHashtagName(String name);

    void delete(Long id, Long userId);
}
