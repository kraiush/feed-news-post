package com.faang.postservice.redis.PubSub.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faang.postservice.dto.event.CommentEvent;
import com.faang.postservice.service.comment.CommentServiceImpl;
import com.faang.postservice.service.outbox.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentListener {

    private final ObjectMapper objectMapper;
    private final CommentServiceImpl commentService;

    private CommentEvent readEvent(String jsonEvent) {
        try {
            log.info("reading message: {} ", jsonEvent);
            return objectMapper.readValue(jsonEvent, CommentEvent.class);
        } catch (JsonProcessingException exception) {
            log.error("message was not downloaded: {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    @EventListener
    public void handleMessage(String jsonEvent) {
        CommentEvent event = readEvent(jsonEvent);
        commentService.saveEvent(EventType.CREATED, event);
        log.info("Comment event: {} was saved", event);
    }
}
