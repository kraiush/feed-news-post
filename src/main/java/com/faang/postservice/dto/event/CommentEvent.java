package com.faang.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CommentEvent {

    private long postId;
    private long commentId;
}
