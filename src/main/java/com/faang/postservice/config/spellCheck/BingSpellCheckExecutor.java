package com.faang.postservice.config.spellCheck;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BingSpellCheckExecutor {

    @Bean(name = "bingSpellAsyncExecutor")
    public ThreadPoolTaskExecutor bingSpellAsyncExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(1000000);
        executor.initialize();
        return executor;
    }
}
