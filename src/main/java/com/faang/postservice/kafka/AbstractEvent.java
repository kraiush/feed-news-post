package com.faang.postservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faang.postservice.model.Event;
import com.faang.postservice.repository.EventRepository;
import com.faang.postservice.service.outbox.EventType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class AbstractEvent<T> {

    private final ObjectMapper objectMapper;
    private final EventRepository eventRepository;

    public <T> void saveEvent(EventType eventType, T event) {
        Event entity = new Event();
        entity.setEventType(eventType);
        try {
            entity.setEventPayload(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        eventRepository.save(entity);
    }
}
