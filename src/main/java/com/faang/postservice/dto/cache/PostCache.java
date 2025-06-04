package com.faang.postservice.dto.cache;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.faang.postservice.validation.hashtag.ValidSetHashtag;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import static com.faang.postservice.validation.utils.DateTimePattern.DATE_TIME_PATTERN;

@RedisHash(value="PostCache", timeToLive = 86400)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCache implements Serializable{

    @Id
    private Long id;
    private Long authorId;
    private String content;

    @Builder.Default
    private Long likeCount= 0L;
    @Builder.Default
    private Long viewCount= 0L;

    private TreeSet<CommentCache> comments;

    @ValidSetHashtag
    private Set<String> hashtagNames;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime publishedAt;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;

    public void incrementLikes() {
        likeCount++;
    }
    public void incrementViews() {
        viewCount++;
    }

}
