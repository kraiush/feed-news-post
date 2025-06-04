package com.faang.postservice.dto.cache;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.faang.postservice.validation.utils.DateTimePattern.DATE_TIME_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCache implements Comparable<CommentCache> {

    private Long id;
    private String content;
    private Long authorId;
    private Long postId;
    private long likesCount;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdAt;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;

    public void incrementLikes() {
        likesCount++;
    }

    @Override
    public int compareTo(CommentCache o) {
        return this.id.compareTo(o.id);
    }

//    public int compareTo(CommentCache o) {
//        return this.createdAt.compareTo(o.createdAt);
//    }
}
