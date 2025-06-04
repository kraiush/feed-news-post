package com.faang.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class PostEvent {

    private long postId;
    private long authorId;
    private List<Long> followerIds;

    public PostEvent(long postId, long authorId) {
        this.postId = postId;
        this.authorId = authorId;
    }
}
