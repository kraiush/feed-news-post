package com.faang.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewEvent {

    private long postId;
    private long authorId;
}
