package com.faang.postservice.redis.cache;

import com.faang.postservice.dto.cache.UserCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class RedisUserRepositoryTest {

    @Value("${spring.data.redis.prefix.userKey}")
    private String userKey;
    private long userId;
    private UserCache userCache;

    @InjectMocks
    private RedisUserRepository redisUserRepository;
    @Mock
    private RedisTemplate<String, UserCache> cacheUserTemplate;
    @Mock
    private RedisTransaction redisTransaction;
    @Mock
    private ValueOperations<String, UserCache> valueOperations;

    @BeforeEach
    public void setUp() {
        userId = 99l;
        MockitoAnnotations.initMocks(this);
        when(cacheUserTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), any(UserCache.class));
        userCache = UserCache.builder().id(userId).username("Buratino").build();
    }

    @Test
    void givenUsertId_getUserCache() {
        ValueOperations<String, UserCache> valueOps = cacheUserTemplate.opsForValue();
        String key = userKey.formatted(userId);
        when(valueOps.get(key)).thenReturn(userCache);
    }
}
