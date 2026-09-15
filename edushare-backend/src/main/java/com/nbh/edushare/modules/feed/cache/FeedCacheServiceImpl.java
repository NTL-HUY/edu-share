package com.nbh.edushare.modules.feed.cache;

import com.nbh.edushare.modules.feed.dto.response.FeedPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
@Slf4j
public class FeedCacheServiceImpl implements FeedCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public FeedPage getFeed(String cacheKey) {
        try {
            return (FeedPage) redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.warn("Redis GET failed for key={}", cacheKey, e);
            return null;
        }
    }

    @Override
    public void putFeed(String cacheKey, FeedPage feedPage, long ttlSeconds) {
        try {
            redisTemplate.opsForValue().set(cacheKey, feedPage, Duration.ofSeconds(ttlSeconds));
        } catch (Exception e) {
            log.warn("Redis SET failed for key={}", cacheKey, e);
        }
    }

    @Override
    public String buildFeedCacheKey(Long userId, String cursor, int limit) {
        return String.format("feed:user:%d:cursor:%s:limit:%d", userId, cursor != null ? cursor : "null", limit);
    }

    @Override
    public String buildDiscoveryCacheKey(String cursorStr, int limit) {
        return "feed:discovery:%s:%d".formatted(cursorStr == null ? "first" : cursorStr, limit);
    }
}
