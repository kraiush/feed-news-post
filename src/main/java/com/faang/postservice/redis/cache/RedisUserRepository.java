package com.faang.postservice.redis.cache;

import com.faang.postservice.dto.cache.UserCache;
import com.faang.postservice.dto.user.UserDto;
import com.faang.postservice.mapper.MapperUtil;
import com.faang.postservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import static java.lang.Boolean.TRUE;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisUserRepository {

    private final UserService userService;

    @Value("${spring.data.redis.prefix.userKey}")
    private String userKey;

    private final RedisTemplate<String, UserCache> cacheUserTemplate;
    private final RedisTransaction redisTransaction;

    public void save(UserCache userCache) {
        String key = userKey.formatted(userCache.getId());
        redisTransaction.futfill(cacheUserTemplate, key, userCache);
    }

    public UserCache findById(long userId) {
        try {
            String key = userKey.formatted(userId);
            return cacheUserTemplate.opsForValue().get(key);
        } catch (Exception ex) {
            log.info("<UserCache> - User with userId: {} is not found", userId);
            return null;
        }
    }

    public boolean hasUserById(long userId) {
        return TRUE.equals(cacheUserTemplate.hasKey(userKey.formatted(userId)));
    }

    public void addLastPostId(long postId, long userId) {
        if (hasUserById(userId)) {
            UserCache user = findById(userId);
            user.setLastPostId(postId);
            save(user);
        } else {
            UserDto userDto = userService.getUser(userId);
            UserCache userCache = MapperUtil.convertClass(userDto, UserCache.class);
            userCache.setLastPostId(postId);
            save(userCache);
        }
    }

    public void deleteById(long userId) {
        cacheUserTemplate.delete(userKey.formatted(userId));
    }
}

