package com.faang.postservice.validation.post;

import com.faang.postservice.exception.ResourceAlreadyExistsException;
import com.faang.postservice.model.Post;
import com.faang.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostValidator {

    private final PostRepository postRepository;

    public void existPost(long postId) {
        if (!postRepository.existsById(postId)) {
            String POST_IS_NOT_FOUND = "<Post> with Id: %d is not found";
            throw new EntityNotFoundException(String.format(POST_IS_NOT_FOUND, postId));
        }
    }

    public void isPublished(Post post) {
        if (post.isPublished()) {
            String POST_IS_PUBLISHED = "<Post> with Id: % is already published!";
            throw new ResourceAlreadyExistsException(String.format(POST_IS_PUBLISHED, post.getId()));
        }
    }

    public void checkAuthor(Post post, Long userId) {
        var authorId= post.getAuthorId();
        if (!Objects.equals(userId, authorId))
        {
            String OPERATION_IS_NOT_AVAILABLE = "There is no <Post> author Id: %d to OPERATE with <Post> Id: %d";
            throw new IllegalArgumentException(String.format(OPERATION_IS_NOT_AVAILABLE, userId, post.getId()));
        }
    }
}