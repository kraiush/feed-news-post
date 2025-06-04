package com.faang.postservice.config.redis;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisCredentials {

    private int port;
    private String host;
    private int connectionPoolSize;
    private int connectionMinimumIdleSize;
    private int retryAttempts;
    private int retryInterval;
    private int timeout;
    private Map<String, String> channels;
}
