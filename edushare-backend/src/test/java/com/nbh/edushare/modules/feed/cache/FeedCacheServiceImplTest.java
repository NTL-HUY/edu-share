package com.nbh.edushare.modules.feed.cache;

import com.nbh.edushare.modules.feed.dto.response.FeedPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedCacheServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private FeedCacheServiceImpl cacheService;

    private FeedPage anyPage() {
        return new FeedPage(List.of(), null, false);
    }

    @Test
    void getFeed_hit_returnsValue() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        FeedPage page = anyPage();
        when(valueOperations.get("key")).thenReturn(page);

        assertSame(page, cacheService.getFeed("key"));
    }

    @Test
    void getFeed_miss_returnsNull() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("key")).thenReturn(null);

        assertNull(cacheService.getFeed("key"));
    }

    @Test
    void getFeed_redisDown_returnsNull() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("key")).thenThrow(new RuntimeException("redis down"));

        assertNull(cacheService.getFeed("key"));
    }

    @Test
    void putFeed_setsWithTtl() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        FeedPage page = anyPage();

        cacheService.putFeed("key", page, 45L);

        verify(valueOperations).set("key", page, Duration.ofSeconds(45L));
    }

    @Test
    void putFeed_redisDown_doesNotThrow() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        FeedPage page = anyPage();
        doThrow(new RuntimeException("redis down"))
                .when(valueOperations).set(eq("key"), eq(page), any(Duration.class));

        assertDoesNotThrow(() -> cacheService.putFeed("key", page, 30L));
    }

    @Test
    void buildFeedCacheKey_withoutCursor() {
        assertEquals("feed:user:7:cursor:null:limit:20", cacheService.buildFeedCacheKey(7L, null, 20));
    }

    @Test
    void buildFeedCacheKey_withCursor() {
        assertEquals("feed:user:7:cursor:abc:limit:20", cacheService.buildFeedCacheKey(7L, "abc", 20));
    }

    @Test
    void buildDiscoveryCacheKey_firstPage() {
        assertEquals("feed:discovery:first:20", cacheService.buildDiscoveryCacheKey(null, 20));
    }

    @Test
    void buildDiscoveryCacheKey_withCursor() {
        assertEquals("feed:discovery:abc:20", cacheService.buildDiscoveryCacheKey("abc", 20));
    }
}