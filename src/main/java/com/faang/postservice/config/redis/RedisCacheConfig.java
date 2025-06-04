package com.faang.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faang.postservice.dto.cache.PostCache;
import com.faang.postservice.dto.cache.UserCache;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
public class RedisCacheConfig {

    @Bean
    public RedisTemplate<String, UserCache> cacheUserTemplate(LettuceConnectionFactory connectionFactory,
                                                               ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, UserCache.class, objectMapper);
    }

    @Bean
    public RedisTemplate<String, PostCache> cachePostTemplate(LettuceConnectionFactory connectionFactory,
                                                              ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, PostCache.class, objectMapper);
    }

    @Bean
    public RedisTemplate<String, Long> newsWireTemplate(LettuceConnectionFactory connectionFactory,
                                                        ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, Long.class, objectMapper);
    }

      private <T> RedisTemplate<String, T> buildRedisTemplate(LettuceConnectionFactory connectionFactory,
                                                            Class<T> clazz,
                                                            ObjectMapper objectMapper) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<T> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, clazz);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        template.setEnableTransactionSupport(true);
        return template;
    }
}
