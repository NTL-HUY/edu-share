package com.nbh.edushare.modules.feed.repository;


import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.feed.pojo.UserFeed;
import com.nbh.edushare.modules.feed.pojo.UserFeedId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserFeedRepository extends JpaRepository<UserFeed, UserFeedId> {
    @Query("""
        SELECT fi FROM UserFeed uf 
        JOIN FeedItem fi ON uf.feedItemId = fi.knowledgeId 
        WHERE uf.userId = :userId 
        ORDER BY fi.sourceCreatedAt DESC, fi.knowledgeId DESC
    """)
    List<FeedItem> findPushedFeedFirstPage(@Param("userId") long userId, Pageable pageable);

    @Query("""
        SELECT fi FROM UserFeed uf 
        JOIN FeedItem fi ON uf.feedItemId = fi.knowledgeId 
        WHERE uf.userId = :userId 
          AND (
            fi.sourceCreatedAt < :createdAt 
            OR (fi.sourceCreatedAt = :createdAt AND fi.knowledgeId < :id)
          )
        ORDER BY fi.sourceCreatedAt DESC, fi.knowledgeId DESC
    """)
    List<FeedItem> findPushedFeed(
            @Param("userId") Long userId,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );
}