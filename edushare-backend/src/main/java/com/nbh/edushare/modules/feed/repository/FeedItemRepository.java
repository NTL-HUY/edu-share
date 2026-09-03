package com.nbh.edushare.modules.feed.repository;

import com.nbh.edushare.modules.feed.pojo.FeedItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedItemRepository extends JpaRepository<FeedItem, Long>, JpaSpecificationExecutor<FeedItem> {

    //    Public feed theo owners (famous / normal followee)
    @Query("""
                SELECT fi FROM FeedItem fi
                WHERE fi.ownerId IN :ownerIds
                  AND fi.isPublic = true
                  AND fi.deletedAt IS NULL
                ORDER BY fi.sourceCreatedAt DESC, fi.knowledgeId DESC
            """)
    List<FeedItem> findLatestPublicByOwners(
            @Param("ownerIds") List<Long> ownerIds,
            Pageable pageable
    );

    @Query("""
                SELECT fi FROM FeedItem fi
                WHERE fi.ownerId IN :ownerIds
                  AND fi.isPublic = true
                  AND fi.deletedAt IS NULL
                  AND (
                    fi.sourceCreatedAt < :createdAt
                    OR (fi.sourceCreatedAt = :createdAt AND fi.knowledgeId < :id)
                  )
                ORDER BY fi.sourceCreatedAt DESC, fi.knowledgeId DESC
            """)
    List<FeedItem> findOlderPublicByOwners(
            @Param("ownerIds") List<Long> ownerIds,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    // ================== Discovery (public, loại trừ các id đã có trong pool) ==================

    @Query("""
                SELECT fi FROM FeedItem fi
                WHERE fi.knowledgeId NOT IN :excludeIds
                  AND fi.isPublic = true
                  AND fi.deletedAt IS NULL
                ORDER BY fi.sourceCreatedAt DESC, fi.knowledgeId DESC
            """)
    List<FeedItem> findLatestPublicDiscovery(
            @Param("excludeIds") List<Long> excludeIds,
            Pageable pageable
    );

    @Query("""
                SELECT fi FROM FeedItem fi
                WHERE fi.knowledgeId NOT IN :excludeIds
                  AND fi.isPublic = true
                  AND fi.deletedAt IS NULL
                  AND (
                    fi.sourceCreatedAt < :createdAt
                    OR (fi.sourceCreatedAt = :createdAt AND fi.knowledgeId < :id)
                  )
                ORDER BY fi.sourceCreatedAt DESC, fi.knowledgeId DESC
            """)
    List<FeedItem> findOlderPublicDiscovery(
            @Param("excludeIds") List<Long> excludeIds,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    @Modifying
    @Query(value = """
            UPDATE feed_item
            SET views_count = views_count + :views,
                vote_score = vote_score + :votes,
                comment_count = comment_count + :comments
            WHERE knowledge_id = :id
            """, nativeQuery = true)
    int adjustCounters(@Param("id") long id, @Param("views") int views,
                       @Param("votes") int votes, @Param("comments") int comments);


    <T> Optional<T> findProjectedByKnowledgeIdAndDeletedAtIsNull(Long knowledgeId, Class<T> type);


    boolean existsByKnowledgeId(Long knowledgeId);

    @Modifying
    @Query("UPDATE FeedItem fi SET fi.deletedAt = :deletedAt WHERE fi.knowledgeId = :knowledgeId AND fi.deletedAt IS NULL")
    int markDeleted(@Param("knowledgeId") Long knowledgeId, @Param("deletedAt") LocalDateTime deletedAt);
}