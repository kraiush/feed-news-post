package com.faang.postservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class AbstractProducer<T> {

    private final NewTopic topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(T event) {
        kafkaTemplate.send(topic.name(), event);
        log.info("Event <{}> was sent to Topic <{}>", event, topic.name());
    }
}
