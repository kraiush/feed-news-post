package com.faang.postservice.kafka;

import com.faang.postservice.model.Event;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventProducer extends AbstractProducer<Event> {

    public KafkaEventProducer(NewTopic outboxTopic,
                              KafkaTemplate<String, Object> kafkaTemplate) {
        super(outboxTopic, kafkaTemplate);
    }
}
