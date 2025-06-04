package com.faang.postservice.dto.post;

import com.faang.postservice.validation.hashtag.ValidSetHashtag;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private Long id;
    private Long authorId;
    @NotBlank(message = "Invalid Content: Empty content")
    @Length(max = 4096, message = "Invalid Content: content exceeds  4096 characters")
    private String content;
    private long likesCount;

    @ValidSetHashtag
    private Set<String> hashtagNames;
}
