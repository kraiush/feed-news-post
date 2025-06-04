package com.faang.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeEvent {

    private long postId;
    private long commentId;
    private long authorId;
}
