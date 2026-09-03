package com.nbh.edushare.modules.feed.repository;


import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.feed.pojo.UserFeed;
import com.nbh.edushare.modules.feed.pojo.UserFeedId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserFeedRepository extends JpaRepository<UserFeed, UserFeedId> {
    @Query("""
        select fi from UserFeed uf
        join FeedItem fi on fi.knowledgeId = uf.feedItemId
        where uf.userId = :userId
          and fi.deletedAt is null
          and (fi.isPublic = true or fi.ownerId = :userId)
        order by fi.sourceCreatedAt desc, fi.knowledgeId desc
        """)
    List<FeedItem> findPushedFeedFirstPage(@Param("userId") Long userId, Pageable pageable);

    @Query("""
        select fi from UserFeed uf
        join FeedItem fi on fi.knowledgeId = uf.feedItemId
        where uf.userId = :userId
          and fi.deletedAt is null
          and (fi.isPublic = true or fi.ownerId = :userId)
          and (fi.sourceCreatedAt < :createdAt
               or (fi.sourceCreatedAt = :createdAt and fi.knowledgeId < :id))
        order by fi.sourceCreatedAt desc, fi.knowledgeId desc
        """)
    List<FeedItem> findPushedFeed(@Param("userId") Long userId,
                                  @Param("createdAt") LocalDateTime createdAt,
                                  @Param("id") Long id,
                                  Pageable pageable);

    @Modifying
    long deleteByFeedItemId(Long feedItemId);
}