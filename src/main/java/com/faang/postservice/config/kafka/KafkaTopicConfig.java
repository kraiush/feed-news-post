package com.faang.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.data.kafka.topic.outbox.name}")
    private String outboxTopic;
    @Value("${spring.data.kafka.topic.outbox.partitions}")
    private int outboxTopicPartitions;
    @Value("${spring.data.kafka.topic.outbox.replicas}")
    private int outboxTopicReplicas;

    @Bean
    public NewTopic outboxTopic() {
        return TopicBuilder
                .name(outboxTopic)
                .partitions(outboxTopicPartitions)
                .replicas(outboxTopicReplicas)
                .build();
    }
}
