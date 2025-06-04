package com.faang.postservice.config.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    private final RedisCredentials credentials;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://%s:%d".formatted(credentials.getHost(), credentials.getPort()))
                .setConnectionPoolSize(credentials.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(credentials.getConnectionMinimumIdleSize())
                .setRetryAttempts(credentials.getRetryAttempts())
                .setRetryInterval(credentials.getRetryInterval())
                .setTimeout(credentials.getTimeout());
        return Redisson.create(config);
    }
}
