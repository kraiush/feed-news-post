package com.faang.postservice.service.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faang.postservice.exception.KafkaProcessingException;
import com.faang.postservice.model.Event;
import com.faang.postservice.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    @Value("${spring.data.kafka.topic.outbox.name}")
    private String outboxTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Scheduled(fixedRateString = "${spring.data.kafka.scheduled}")
    public void eventProcessing() {
        try {
            List<Event> listOfEventEntities = new ArrayList<>();
            eventRepository.findAll().forEach(listOfEventEntities::add);
            log.info("Number of outbox events: {}", listOfEventEntities.size());
            if (!listOfEventEntities.isEmpty()) {
                for (Event eventEntity : listOfEventEntities) {
                    if (eventEntity.getEventType() != null) {
                        String eventType = determineEventType(eventEntity.getEventType());
                        log.info("OutBox <> Sending event to Kafka: {}", eventEntity);
                        sendEventToKafka(eventType, eventEntity);
                    }
                }
            }
        } catch (Exception e) {
            throw new KafkaProcessingException("OutBox <> Kafka can't transmit the event!");
        }
    }

    private void sendEventToKafka(String eventType, Event eventEntity) {
        try {
            CompletableFuture<SendResult<String, Object>> sendResult = kafkaTemplate.send(
                    outboxTopic,
                    eventType,
                    objectMapper.writeValueAsBytes(eventEntity));
            SendResult<String, Object> result = sendResult.get();
            delete(eventEntity.getId());
            log.info("OutBox <> Sent event to partition: {}", result.getRecordMetadata().partition());
        } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
            log.error("OutBox <> Error sending event to Kafka: {}", e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        Event entity = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("<Event > Id " + id + " is not found"));
        eventRepository.deleteById(id);
    }

    private String determineEventType(EventType eventType) {
        return switch (eventType) {
            case PUBLISHED -> "PUBLISHED";
            case UPDATED -> "UPDATED";
            case COMMENTED -> "COMMENTED";
            case COMMENT_UPDATED -> "COMMENT_UPDATED";
            case LIKED_POST -> "LIKED_POST";
            case LIKED_COMMENT -> "LIKED_COMMENT";
            case VIEWED -> "VIEWED";
            default -> null;
        };
    }
}
