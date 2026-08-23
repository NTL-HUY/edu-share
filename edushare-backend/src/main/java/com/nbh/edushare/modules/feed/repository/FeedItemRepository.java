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
    @Query("""
        SELECT fi FROM FeedItem fi 
        WHERE fi.ownerId IN :ownerIds 
        ORDER BY fi.sourceCreatedAt DESC, fi.knowledgeId DESC
    """)
    List<FeedItem> findLatestByOwners(
            @Param("ownerIds") List<Long> ownerIds,
            Pageable pageable
    );

    @Query("""
        SELECT fi FROM FeedItem fi 
        WHERE fi.ownerId IN :ownerIds 
          AND (
            fi.sourceCreatedAt < :createdAt 
            OR (fi.sourceCreatedAt = :createdAt AND fi.knowledgeId < :id)
          )
        ORDER BY fi.sourceCreatedAt DESC, fi.knowledgeId DESC
    """)
    List<FeedItem> findOlderByOwners(
            @Param("ownerIds") List<Long> ownerIds,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("id") Long id,
            Pageable pageable
    );

    @Query("""
        SELECT fi FROM FeedItem fi 
        WHERE fi.knowledgeId NOT IN :excludeIds 
        ORDER BY fi.sourceCreatedAt DESC, fi.knowledgeId DESC
    """)
    List<FeedItem> findLastestDiscovery(
            @Param("excludeIds") List<Long> excludeIds,
            Pageable pageable
    );

    @Query("""
        SELECT fi FROM FeedItem fi 
        WHERE fi.knowledgeId NOT IN :excludeIds 
          AND (
            fi.sourceCreatedAt < :createdAt 
            OR (fi.sourceCreatedAt = :createdAt AND fi.knowledgeId < :id)
          )
        ORDER BY fi.sourceCreatedAt DESC, fi.knowledgeId DESC
    """)
    List<FeedItem> findOlderDiscovery(
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

    <T> Optional<T> findProjectedByKnowledgeId(Long knowledgeId, Class<T> type);

    boolean existsByKnowledgeId(Long knowledgeId);
}