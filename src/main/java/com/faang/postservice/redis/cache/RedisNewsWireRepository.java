package com.faang.postservice.redis.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.client.codec.LongCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisNewsWireRepository {

    @Value("${spring.data.redis.prefix.newsWireKey}")
    private String newsWireKey;
    @Value("${spring.data.redis.postsBoxSize}")
    private Integer postsBoxSize;
    @Value("${spring.data.redis.postsBatchSize}")
    private Integer postsBatchSize;

    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Long> newsWireTemplate;

    public void addPostToUser(long postId, long userId, Double datetime) {

        ZSetOperations<String, Long> zSet = newsWireTemplate.opsForZSet();
        String key = newsWireKey.formatted(userId);
        RLock lock = redissonClient.getLock(key + "-lock");
        try {
            lock.lock();
            zSet.add(key, postId, datetime);
            Long countZ = zSet.zCard(key);
            if (countZ != null && countZ > postsBoxSize) {
                newsWireTemplate.opsForZSet().removeRange(key, 0, 0);
            }
        } catch (Exception ex) {
            log.error("Redis <> Exception occurred: " + ex.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Async
    public void addPostToFollowers(long postId, List<Long> userIds, Double datetime) {
        RBatch batch = redissonClient.createBatch(BatchOptions.defaults()
                .retryAttempts(3)
                .retryInterval(2, TimeUnit.SECONDS)
                .responseTimeout(3, TimeUnit.SECONDS)
                .skipResult());
        for (Long userId : userIds) {
            String key = newsWireKey.formatted(userId);
            RLock lock = redissonClient.getLock(key + "-RLock");
            try {
                lock.lock();
                batch.getScoredSortedSet(key, LongCodec.INSTANCE).addAsync(datetime, postId);
                RScoredSortedSet<Long> scoredSet = redissonClient.getScoredSortedSet(key);
//                Long countZ = newsWireTemplate.opsForZSet().zCard(key);
                if (scoredSet.size() > postsBoxSize) {
                    scoredSet.removeRangeByRankAsync(0, 0);
                }
            } catch (Exception ex) {
                log.error("Redis <> Exception occurred: " + ex.getMessage());
            } finally {
                lock.unlock();
            }
        }
        batch.execute();
    }

    public Set<Long> getCachedRecords(long userId, Long lastPostId) {
        ZSetOperations<String, Long> zSet = newsWireTemplate.opsForZSet();
        String key = newsWireKey.formatted(userId);
        if (lastPostId == 0) {
            return zSet.range(key, 0, postsBatchSize - 1);
        } else {
            Double lastPostScore = zSet.score(key, lastPostId);
            if (lastPostScore != null) {
                double nowTs = System.currentTimeMillis();
                return zSet.rangeByScore(key, lastPostScore, nowTs, 0, postsBatchSize);
            }
            log.info("Redis <> No records found for userId: {}", userId);
            return null;
        }
    }

    public Boolean cacheIsEmpty() {
        return newsWireTemplate.keys("news-wire-*").isEmpty() ? true : false;
    }
}
