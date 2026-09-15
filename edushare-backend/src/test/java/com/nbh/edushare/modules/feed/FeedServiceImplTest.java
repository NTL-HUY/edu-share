package com.nbh.edushare.modules.feed;

import com.nbh.edushare.modules.feed.cache.FeedCacheService;
import com.nbh.edushare.modules.feed.dto.request.FeedSearchInput;
import com.nbh.edushare.modules.feed.dto.response.FeedPage;
import com.nbh.edushare.modules.feed.dto.response.FeedSearchResult;
import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.feed.query.FeedQueryService;
import com.nbh.edushare.modules.feed.repository.FeedItemRepository;
import com.nbh.edushare.modules.feed.util.FeedCursor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceImplTest {

    @Mock
    private FeedItemRepository feedItemRepository;
    @Mock
    private FeedQueryService feedQueryService;
    @Mock
    private FeedCacheService feedCacheService;

    @InjectMocks
    private FeedServiceImpl feedService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedService, "feedCacheTtlSeconds", 30L);
        ReflectionTestUtils.setField(feedService, "discoveryCacheTtlSeconds", 60L);
    }

    private FeedItem feedItem(long id, LocalDateTime createdAt) {
        FeedItem item = new FeedItem();
        item.setKnowledgeId(id);
        item.setSourceCreatedAt(createdAt);
        return item;
    }

    @Test
    void getFeed_anonymousUser_returnsDiscoveryFeed() {
        LocalDateTime t = LocalDateTime.of(2026, 1, 1, 10, 0);
        String cursorStr = new FeedCursor(t, 5L).encode();
        FeedPage page = new FeedPage(List.of(feedItem(5L, t)), null, false);

        when(feedCacheService.buildDiscoveryCacheKey(cursorStr, 10)).thenReturn("key");
        when(feedCacheService.getFeed("key")).thenReturn(null);
        when(feedQueryService.loadDiscoveryFeed(eq(new FeedCursor(t, 5L)), eq(10))).thenReturn(page);

        FeedPage result = feedService.getFeed(null, cursorStr, 10);

        assertSame(page, result);
        verify(feedQueryService, never()).loadFeedByUserId(any(), any(), anyInt());
        verify(feedCacheService).putFeed("key", page, 60L);
    }

    @Test
    void getFeed_anonymousUser_cacheHit_returnsCached() {
        FeedPage page = new FeedPage(List.of(feedItem(1L, LocalDateTime.now())), null, false);

        when(feedCacheService.buildDiscoveryCacheKey(null, 10)).thenReturn("key");
        when(feedCacheService.getFeed("key")).thenReturn(page);

        FeedPage result = feedService.getFeed(null, null, 10);

        assertSame(page, result);
        verify(feedQueryService, never()).loadDiscoveryFeed(any(), anyInt());
        verify(feedCacheService, never()).putFeed(anyString(), any(), anyLong());
    }

    @Test
    void getFeed_user_cacheHit_returnsCached() {
        FeedPage page = new FeedPage(List.of(feedItem(1L, LocalDateTime.now())), null, false);

        when(feedCacheService.buildFeedCacheKey(1L, null, 10)).thenReturn("key");
        when(feedCacheService.getFeed("key")).thenReturn(page);

        FeedPage result = feedService.getFeed(1L, null, 10);

        assertSame(page, result);
        verify(feedQueryService, never()).loadFeedByUserId(any(), any(), anyInt());
        verify(feedCacheService, never()).putFeed(anyString(), any(), anyLong());
    }

    @Test
    void getFeed_user_cacheMiss_loadsAndCaches() {
        FeedPage page = new FeedPage(List.of(feedItem(1L, LocalDateTime.now())), null, false);

        when(feedCacheService.buildFeedCacheKey(1L, null, 10)).thenReturn("key");
        when(feedCacheService.getFeed("key")).thenReturn(null);
        when(feedQueryService.loadFeedByUserId(eq(1L), isNull(), eq(10))).thenReturn(page);

        FeedPage result = feedService.getFeed(1L, null, 10);

        assertSame(page, result);
        verify(feedQueryService).loadFeedByUserId(eq(1L), isNull(), eq(10));
        verify(feedCacheService).putFeed("key", page, 30L);
    }

    @Test
    void getFeed_user_decodesCursor_andPassesToQuery() {
        LocalDateTime t = LocalDateTime.of(2026, 3, 4, 12, 30);
        String cursorStr = new FeedCursor(t, 9L).encode();
        FeedPage page = new FeedPage(List.of(feedItem(9L, t)), null, false);

        when(feedCacheService.buildFeedCacheKey(1L, cursorStr, 10)).thenReturn("key");
        when(feedCacheService.getFeed("key")).thenReturn(null);
        when(feedQueryService.loadFeedByUserId(eq(1L), eq(new FeedCursor(t, 9L)), eq(10))).thenReturn(page);

        feedService.getFeed(1L, cursorStr, 10);

        verify(feedQueryService).loadFeedByUserId(eq(1L), eq(new FeedCursor(t, 9L)), eq(10));
    }

    @Test
    void searchFeed_delegatesToRepository_andMapsResult() {
        FeedSearchInput input = new FeedSearchInput(null, null, null, null, 0, 10, null);
        Pageable pageable = PageRequest.of(0, 10);
        FeedItem item = feedItem(1L, LocalDateTime.now());
        when(feedItemRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item), pageable, 1));

        FeedSearchResult result = feedService.searchFeed(input, pageable);

        assertEquals(1, result.totalCount());
        assertEquals(List.of(item), result.items());
        assertEquals(0, result.page());
        assertEquals(1, result.totalPages());
    }

    @Test
    void findProjectedById_delegatesToRepository() {
        when(feedItemRepository.findProjectedByKnowledgeIdAndDeletedAtIsNull(7L, String.class))
                .thenReturn(Optional.of("ok"));

        assertEquals(Optional.of("ok"), feedService.findProjectedById(7L, String.class));
    }

    @Test
    void adjustCounters_delegatesToRepository() {
        when(feedItemRepository.adjustCounters(1L, 5, 2, 3)).thenReturn(1);

        assertEquals(1, feedService.adjustCounters(1L, 5, 2, 3));
    }

    @Test
    void existsByKnowledgeId_delegatesToRepository() {
        when(feedItemRepository.existsByKnowledgeId(3L)).thenReturn(true);

        assertEquals(true, feedService.existsByKnowledgeId(3L));
    }
}