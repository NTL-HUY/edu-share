package com.nbh.edushare.modules.feed.query;

import com.nbh.edushare.modules.feed.dto.response.FeedPage;
import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.feed.repository.FeedItemRepository;
import com.nbh.edushare.modules.feed.repository.UserFeedRepository;
import com.nbh.edushare.modules.feed.util.FeedCursor;
import com.nbh.edushare.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FeedQueryServiceImpl implements FeedQueryService {
    private final FeedItemRepository feedItemRepository;
    private final UserFeedRepository userFeedRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    @Override
    public FeedPage loadFeedByUserId(Long userId, FeedCursor cursor, int limit) {
        int effectiveLimit = limit + 1;
        Pageable pageable = PageRequest.of(0, effectiveLimit);

        Map<Long, FeedItem> pool = new LinkedHashMap<>();

        List<FeedItem> pushed = (cursor == null)
                ? userFeedRepository.findPushedFeedFirstPage(userId, pageable)
                : userFeedRepository.findPushedFeed(userId, cursor.createdAt(), cursor.id(), pageable);
        putAll(pool, pushed);

        List<Long> famousIds = userService.findFamousFolloweeIds(userId);
        if (!famousIds.isEmpty()) {
            List<FeedItem> kolFeed = (cursor == null)
                    ? feedItemRepository.findLatestPublicByOwners(famousIds, pageable)
                    : feedItemRepository.findOlderPublicByOwners(famousIds, cursor.createdAt(), cursor.id(), pageable);
            putAll(pool, kolFeed);
        }

        if (pool.size() < effectiveLimit) {
            List<Long> normalIds = userService.findNormalFolloweeIds(userId);
            if (!normalIds.isEmpty()) {
                Pageable fallbackPageable = PageRequest.of(0, effectiveLimit - pool.size());
                List<FeedItem> fallback = (cursor == null)
                        ? feedItemRepository.findLatestPublicByOwners(normalIds, fallbackPageable)
                        : feedItemRepository.findOlderPublicByOwners(normalIds, cursor.createdAt(), cursor.id(), fallbackPageable);
                putAll(pool, fallback);
            }
        }

        if (pool.size() < effectiveLimit) {
            List<Long> excludeIds = pool.isEmpty() ? List.of(-1L) : new ArrayList<>(pool.keySet());
            Pageable discoveryPageable = PageRequest.of(0, effectiveLimit - pool.size());
            List<FeedItem> discovery = (cursor == null)
                    ? feedItemRepository.findLatestPublicDiscovery(excludeIds, discoveryPageable)
                    : feedItemRepository.findOlderPublicDiscovery(excludeIds, cursor.createdAt(), cursor.id(), discoveryPageable);
            putAll(pool, discovery);
        }

        List<FeedItem> sorted = pool.values().stream()
                .sorted(Comparator.comparing(FeedItem::getSourceCreatedAt)
                        .thenComparing(FeedItem::getKnowledgeId)
                        .reversed())
                .toList();

        boolean hasMore = sorted.size() > limit;
        List<FeedItem> finalList = hasMore ? sorted.subList(0, limit) : sorted;

        String nextCursor = finalList.isEmpty() ? null
                : new FeedCursor(
                finalList.getLast().getSourceCreatedAt(),
                finalList.getLast().getKnowledgeId()
        ).encode();

        return new FeedPage(finalList, nextCursor, hasMore);
    }

    @Transactional(readOnly = true)
    @Override
    public FeedPage loadDiscoveryFeed(FeedCursor cursor, int limit) {
        Pageable pageable = PageRequest.of(0, limit + 1);

        List<FeedItem> discovery = (cursor == null)
                ? feedItemRepository.findLatestPublicDiscovery(List.of(-1L), pageable)
                : feedItemRepository.findOlderPublicDiscovery(List.of(-1L), cursor.createdAt(), cursor.id(), pageable);

        boolean hasMore = discovery.size() > limit;
        List<FeedItem> finalList = hasMore ? discovery.subList(0, limit) : discovery;

        String nextCursor = finalList.isEmpty() ? null
                : new FeedCursor(
                finalList.getLast().getSourceCreatedAt(),
                finalList.getLast().getKnowledgeId()
        ).encode();

        return new FeedPage(finalList, nextCursor, hasMore);
    }

    private void putAll(Map<Long, FeedItem> pool, List<FeedItem> items) {
        for (FeedItem item : items) {
            pool.putIfAbsent(item.getKnowledgeId(), item);
        }
    }
}
