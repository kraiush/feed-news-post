package com.faang.postservice.dto.event;

import com.faang.postservice.service.outbox.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventDomain {

    private EventType eventType;
    private String eventPayload;
}
