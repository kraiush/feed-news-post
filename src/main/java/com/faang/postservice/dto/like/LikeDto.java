package com.faang.postservice.dto.like;

import com.faang.postservice.validation.like.LikeComment;
import com.faang.postservice.validation.like.LikePost;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {

    private Long id;
    @Min(value = 0, message = "User ID must be positive", groups = {LikePost.class, LikeComment.class})
    @NotNull(message = "User ID must be provided", groups = {LikePost.class, LikeComment.class})
    private long userId;
    @Positive(message = "Post Id not specified or negative", groups = LikePost.class)
    private long postId;
    @Positive(message = "Comment Id not specified or negative", groups = LikeComment.class)
    private long commentId;
}
