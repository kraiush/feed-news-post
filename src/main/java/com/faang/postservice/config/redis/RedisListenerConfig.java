package com.faang.postservice.config.redis;

import com.faang.postservice.redis.PubSub.listener.CommentListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisListenerConfig {

    private final RedisCredentials credentials;
    private final ChannelTopic commentChannelTopic;

    public RedisListenerConfig(RedisCredentials credentials, ChannelTopic commentChannelTopic) {
        this.credentials = credentials;
        this.commentChannelTopic = commentChannelTopic;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            CommentListener commentListener,
            RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(commentEventListener(commentListener), commentChannelTopic);
        return container;
    }

    @Bean
    public MessageListenerAdapter commentEventListener(CommentListener commentListener) {
        return new MessageListenerAdapter(commentListener);
    }
}
