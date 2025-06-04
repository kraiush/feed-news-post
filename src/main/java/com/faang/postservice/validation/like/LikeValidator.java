package com.faang.postservice.validation.like;

import com.faang.postservice.model.Like;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class LikeValidator {

    public void checkDuplicateLike(Optional<Like> optionalLike) {
        optionalLike.ifPresent(like -> {
            throw new IllegalArgumentException("Duplicate like! ");
        });
    }

    public void checkAvailabilityLike(Optional<Like> optionalLike) {
        if (optionalLike.isEmpty()) {
            throw new IllegalArgumentException("Like was not found using the passed identifiers");
        }
    }

    public void checkAuthor(Like like, Long userId) {
        var authorId= like.getUserId();
        if (!Objects.equals(userId, authorId))
        {
            String OPERATION_IS_NOT_AVAILABLE = "There is no <Like> author Id: %d to OPERATE <Like> with Id: %d";
            throw new IllegalArgumentException(String.format(OPERATION_IS_NOT_AVAILABLE, userId, like.getId()));
        }
    }
}
