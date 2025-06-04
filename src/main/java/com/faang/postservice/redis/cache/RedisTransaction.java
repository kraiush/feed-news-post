package com.faang.postservice.redis.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTransaction {

    @Retryable(retryFor = OptimisticLockingFailureException.class,
            maxAttemptsExpression = "3")
    @Transactional
    public void futfill(RedisTemplate<?, ?> template, String key, Object objectCache) {
        template.execute(new SessionCallback() {
            public Object execute(RedisOperations operations) {
                try {
                    operations.multi();
                    log.info("Redis <> Processed cache is: " + objectCache);
                    operations.opsForValue().set(key, objectCache);
                    return null;
                } catch (Exception e) {
                    log.error("Redis <> Exception occurred: " + e.getMessage());
                    operations.discard();
                    return null;
                }
            }
        });
    }
}