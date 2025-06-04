package com.faang.postservice.dto.comment;

import com.faang.postservice.validation.comment.CommentCreate;
import com.faang.postservice.validation.comment.CommentUpdate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @Min(value = 1, groups = {CommentUpdate.class})
    private Long id;
    @NotBlank(message = "Invalid Content: Empty content")
    @Size(min = 3, max = 4096, message = "Invalid Content: content exceeds  4096 characters",
            groups = {CommentCreate.class, CommentUpdate.class})
    private String content;
    private Long authorId;
    @Positive(message = "Invalid Post: Post Id not specified or negative", groups = {CommentCreate.class})
    private Long postId;
}


