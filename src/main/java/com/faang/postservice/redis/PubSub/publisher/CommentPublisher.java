package com.faang.postservice.redis.PubSub.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faang.postservice.dto.event.CommentEvent;
import com.faang.postservice.exception.RedisPublishingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentPublisher {

    private final ObjectMapper objectMapper;

    @Qualifier("commentChannelTopic")
    ChannelTopic channelTopic;

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(CommentEvent event) {
        objectMapper.findAndRegisterModules();
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelTopic.getTopic(), message);
            redisTemplate.opsForValue().set(channelTopic.getTopic(), message);
            log.info("The message published to Redis topic {}: {}", channelTopic.getTopic(), message);
        } catch (JsonProcessingException exception) {
            String errorMessage = "The message could not be serialized: " + event;
            log.error(errorMessage, exception);
            throw new RedisPublishingException(errorMessage + " - exception: " + exception.getMessage());
        } catch (Exception exception) {
            String errorMessage = "Failed to publish the nessage to Redis: " + event;
            log.error(errorMessage, exception);
            throw new RedisPublishingException(errorMessage + " - exception: " + exception.getMessage());
        }
    }
}
