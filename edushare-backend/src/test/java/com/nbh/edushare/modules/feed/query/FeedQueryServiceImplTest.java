package com.nbh.edushare.modules.feed.query;

import com.nbh.edushare.modules.feed.dto.response.FeedPage;
import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.feed.repository.FeedItemRepository;
import com.nbh.edushare.modules.feed.repository.UserFeedRepository;
import com.nbh.edushare.modules.feed.util.FeedCursor;
import com.nbh.edushare.modules.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedQueryServiceImplTest {

    @Mock
    private FeedItemRepository feedItemRepository;
    @Mock
    private UserFeedRepository userFeedRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private FeedQueryServiceImpl feedQueryService;

    private FeedItem feedItem(long id, LocalDateTime createdAt) {
        FeedItem item = new FeedItem();
        item.setKnowledgeId(id);
        item.setSourceCreatedAt(createdAt);
        return item;
    }

    @Test
    void loadFeedByUserId_pushedFeedOnly_fillsPoolToLimit() {
        LocalDateTime t3 = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime t2 = t3.minusMinutes(1);
        LocalDateTime t1 = t3.minusMinutes(2);
        FeedItem a = feedItem(3L, t3);
        FeedItem b = feedItem(2L, t2);
        FeedItem c = feedItem(1L, t1);

        when(userFeedRepository.findPushedFeedFirstPage(eq(1L), any())).thenReturn(List.of(a, b, c));
        when(userService.findFamousFolloweeIds(1L)).thenReturn(List.of());

        FeedPage page = feedQueryService.loadFeedByUserId(1L, null, 2);

        assertEquals(List.of(a, b), page.items());
        assertTrue(page.hasMore());
        FeedCursor decoded = FeedCursor.decode(page.nextCursor());
        assertEquals(t2, decoded.createdAt());
        assertEquals(2L, decoded.id());
        verify(userFeedRepository).findPushedFeedFirstPage(eq(1L), any());
        verify(userService).findFamousFolloweeIds(1L);
        verify(userService, never()).findNormalFolloweeIds(any());
        verify(feedItemRepository, never()).findLatestPublicDiscovery(any(), any());
    }

    @Test
    void loadFeedByUserId_dedupAcrossSources_pushedWins() {
        LocalDateTime t3 = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime t2 = t3.minusMinutes(1);
        LocalDateTime t1 = t3.minusMinutes(2);
        FeedItem a = feedItem(3L, t3);
        FeedItem b = feedItem(2L, t2);
        FeedItem c = feedItem(1L, t1);

        when(userFeedRepository.findPushedFeedFirstPage(eq(1L), any())).thenReturn(List.of(a, b));
        when(userService.findFamousFolloweeIds(1L)).thenReturn(List.of(10L));
        when(feedItemRepository.findLatestPublicByOwners(eq(List.of(10L)), any()))
                .thenReturn(List.of(b, c));

        FeedPage page = feedQueryService.loadFeedByUserId(1L, null, 2);

        assertEquals(List.of(a, b), page.items());
        assertTrue(page.hasMore());
        verify(feedItemRepository).findLatestPublicByOwners(eq(List.of(10L)), any());
    }

    @Test
    void loadFeedByUserId_fallbackToNormalFollowees_usesRemainingCapacity() {
        LocalDateTime t3 = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime t2 = t3.minusMinutes(1);
        LocalDateTime t1 = t3.minusMinutes(2);
        FeedItem a = feedItem(3L, t3);
        FeedItem b = feedItem(2L, t2);
        FeedItem c = feedItem(1L, t1);

        when(userFeedRepository.findPushedFeedFirstPage(eq(1L), any())).thenReturn(List.of(a));
        when(userService.findFamousFolloweeIds(1L)).thenReturn(List.of());
        when(userService.findNormalFolloweeIds(1L)).thenReturn(List.of(100L));
        when(feedItemRepository.findLatestPublicByOwners(eq(List.of(100L)), any()))
                .thenReturn(List.of(b, c));

        FeedPage page = feedQueryService.loadFeedByUserId(1L, null, 2);

        assertEquals(List.of(a, b), page.items());
        assertTrue(page.hasMore());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(feedItemRepository).findLatestPublicByOwners(eq(List.of(100L)), captor.capture());
        assertEquals(PageRequest.of(0, 2), captor.getValue());
        verify(feedItemRepository, never()).findLatestPublicDiscovery(any(), any());
    }

    @Test
    void loadFeedByUserId_fallbackToDiscovery_excludesPoolIds() {
        LocalDateTime t3 = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime t2 = t3.minusMinutes(1);
        LocalDateTime t1 = t3.minusMinutes(2);
        FeedItem a = feedItem(3L, t3);
        FeedItem b = feedItem(2L, t2);
        FeedItem c = feedItem(1L, t1);

        when(userFeedRepository.findPushedFeedFirstPage(eq(1L), any())).thenReturn(List.of(a));
        when(userService.findFamousFolloweeIds(1L)).thenReturn(List.of());
        when(userService.findNormalFolloweeIds(1L)).thenReturn(List.of(100L));
        when(feedItemRepository.findLatestPublicByOwners(eq(List.of(100L)), any()))
                .thenReturn(List.of(b));
        when(feedItemRepository.findLatestPublicDiscovery(eq(List.of(3L, 2L)), any()))
                .thenReturn(List.of(c));

        FeedPage page = feedQueryService.loadFeedByUserId(1L, null, 2);

        assertEquals(List.of(a, b), page.items());
        assertTrue(page.hasMore());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(feedItemRepository).findLatestPublicDiscovery(eq(List.of(3L, 2L)), captor.capture());
        assertEquals(PageRequest.of(0, 1), captor.getValue());
    }

    @Test
    void loadFeedByUserId_discoveryFallback_poolEmpty_usesMinusOne() {
        LocalDateTime t2 = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime t1 = t2.minusMinutes(1);
        FeedItem a = feedItem(3L, t2);
        FeedItem b = feedItem(2L, t1);

        when(userFeedRepository.findPushedFeedFirstPage(eq(1L), any())).thenReturn(List.of());
        when(userService.findFamousFolloweeIds(1L)).thenReturn(List.of());
        when(userService.findNormalFolloweeIds(1L)).thenReturn(List.of());
        when(feedItemRepository.findLatestPublicDiscovery(eq(List.of(-1L)), any()))
                .thenReturn(List.of(a, b));

        FeedPage page = feedQueryService.loadFeedByUserId(1L, null, 2);

        assertEquals(List.of(a, b), page.items());
        assertFalse(page.hasMore());
        FeedCursor decoded = FeedCursor.decode(page.nextCursor());
        assertEquals(t1, decoded.createdAt());
        assertEquals(2L, decoded.id());
    }

    @Test
    void loadFeedByUserId_partialFill_belowEffectiveLimit_hasNextCursor() {
        LocalDateTime t2 = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime t1 = t2.minusMinutes(1);
        FeedItem a = feedItem(3L, t2);
        FeedItem b = feedItem(2L, t1);

        when(userFeedRepository.findPushedFeedFirstPage(eq(1L), any())).thenReturn(List.of(a, b));
        when(userService.findFamousFolloweeIds(1L)).thenReturn(List.of());
        when(userService.findNormalFolloweeIds(1L)).thenReturn(List.of());
        when(feedItemRepository.findLatestPublicDiscovery(eq(List.of(3L, 2L)), any()))
                .thenReturn(List.of());

        FeedPage page = feedQueryService.loadFeedByUserId(1L, null, 5);

        assertEquals(List.of(a, b), page.items());
        assertFalse(page.hasMore());
        FeedCursor decoded = FeedCursor.decode(page.nextCursor());
        assertEquals(t1, decoded.createdAt());
        assertEquals(2L, decoded.id());
    }

    @Test
    void loadFeedByUserId_cursorPagination_usesCursorVariants() {
        LocalDateTime t = LocalDateTime.of(2026, 1, 1, 10, 0);
        FeedCursor cursor = new FeedCursor(t, 5L);
        FeedItem a = feedItem(4L, t.minusMinutes(1));
        FeedItem b = feedItem(3L, t.minusMinutes(2));
        FeedItem c = feedItem(2L, t.minusMinutes(3));

        when(userFeedRepository.findPushedFeed(eq(1L), eq(t), eq(5L), any())).thenReturn(List.of(a));
        when(userService.findFamousFolloweeIds(1L)).thenReturn(List.of(10L));
        when(feedItemRepository.findOlderPublicByOwners(eq(List.of(10L)), eq(t), eq(5L), any()))
                .thenReturn(List.of(b));
        when(userService.findNormalFolloweeIds(1L)).thenReturn(List.of(100L));
        when(feedItemRepository.findOlderPublicByOwners(eq(List.of(100L)), eq(t), eq(5L), any()))
                .thenReturn(List.of(c));

        FeedPage page = feedQueryService.loadFeedByUserId(1L, cursor, 2);

        assertEquals(List.of(a, b), page.items());
        assertTrue(page.hasMore());
        verify(userFeedRepository).findPushedFeed(eq(1L), eq(t), eq(5L), any());
        verify(feedItemRepository).findOlderPublicByOwners(eq(List.of(10L)), eq(t), eq(5L), any());
        verify(feedItemRepository).findOlderPublicByOwners(eq(List.of(100L)), eq(t), eq(5L), any());
        verify(feedItemRepository, never()).findOlderPublicDiscovery(any(), any(), any(), any());
    }

    @Test
    void loadFeedByUserId_sortsDescending_thenByKnowledgeIdDesc() {
        LocalDateTime t = LocalDateTime.of(2026, 1, 1, 10, 0);
        FeedItem i1 = feedItem(1L, t);
        FeedItem i5 = feedItem(5L, t);
        FeedItem i3 = feedItem(3L, t);

        when(userFeedRepository.findPushedFeedFirstPage(eq(1L), any())).thenReturn(List.of(i1, i5, i3));
        when(userService.findFamousFolloweeIds(1L)).thenReturn(List.of());
        when(userService.findNormalFolloweeIds(1L)).thenReturn(List.of());
        when(feedItemRepository.findLatestPublicDiscovery(eq(List.of(1L, 5L, 3L)), any()))
                .thenReturn(List.of());

        FeedPage page = feedQueryService.loadFeedByUserId(1L, null, 5);

        assertEquals(List.of(i5, i3, i1), page.items());
        assertFalse(page.hasMore());
    }

    @Test
    void loadFeedByUserId_allEmpty_returnsEmptyPage() {
        when(userFeedRepository.findPushedFeedFirstPage(eq(1L), any())).thenReturn(List.of());
        when(userService.findFamousFolloweeIds(1L)).thenReturn(List.of());
        when(userService.findNormalFolloweeIds(1L)).thenReturn(List.of());
        when(feedItemRepository.findLatestPublicDiscovery(eq(List.of(-1L)), any()))
                .thenReturn(List.of());

        FeedPage page = feedQueryService.loadFeedByUserId(1L, null, 10);

        assertTrue(page.items().isEmpty());
        assertFalse(page.hasMore());
        assertNull(page.nextCursor());
    }

    @Test
    void loadDiscoveryFeed_firstPage_hasMore_returnsNextCursor() {
        LocalDateTime t3 = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime t2 = t3.minusMinutes(1);
        LocalDateTime t1 = t3.minusMinutes(2);
        FeedItem a = feedItem(3L, t3);
        FeedItem b = feedItem(2L, t2);
        FeedItem c = feedItem(1L, t1);

        when(feedItemRepository.findLatestPublicDiscovery(eq(List.of(-1L)), any()))
                .thenReturn(List.of(a, b, c));

        FeedPage page = feedQueryService.loadDiscoveryFeed(null, 2);

        assertEquals(List.of(a, b), page.items());
        assertTrue(page.hasMore());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(feedItemRepository).findLatestPublicDiscovery(eq(List.of(-1L)), captor.capture());
        assertEquals(PageRequest.of(0, 3), captor.getValue());
        FeedCursor decoded = FeedCursor.decode(page.nextCursor());
        assertEquals(t2, decoded.createdAt());
        assertEquals(2L, decoded.id());
    }

    @Test
    void loadDiscoveryFeed_cursorPagination_usesOlderVariant() {
        LocalDateTime t = LocalDateTime.of(2026, 1, 1, 10, 0);
        FeedCursor cursor = new FeedCursor(t, 5L);
        FeedItem a = feedItem(3L, t.minusMinutes(1));

        when(feedItemRepository.findOlderPublicDiscovery(eq(List.of(-1L)), eq(t), eq(5L), any()))
                .thenReturn(List.of(a));

        FeedPage page = feedQueryService.loadDiscoveryFeed(cursor, 10);

        assertEquals(List.of(a), page.items());
        assertFalse(page.hasMore());
        assertNotNull(page.nextCursor());
    }

    @Test
    void loadDiscoveryFeed_exactLimit_noHasMore() {
        LocalDateTime t2 = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime t1 = t2.minusMinutes(1);
        FeedItem a = feedItem(3L, t2);
        FeedItem b = feedItem(2L, t1);

        when(feedItemRepository.findLatestPublicDiscovery(eq(List.of(-1L)), any()))
                .thenReturn(List.of(a, b));

        FeedPage page = feedQueryService.loadDiscoveryFeed(null, 2);

        assertEquals(List.of(a, b), page.items());
        assertFalse(page.hasMore());
        FeedCursor decoded = FeedCursor.decode(page.nextCursor());
        assertEquals(t1, decoded.createdAt());
        assertEquals(2L, decoded.id());
    }
}