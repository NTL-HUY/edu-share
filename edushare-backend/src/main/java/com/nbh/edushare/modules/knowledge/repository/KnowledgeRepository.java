package com.nbh.edushare.modules.knowledge.repository;

import com.nbh.edushare.modules.knowledge.dto.response.KnowledgeManageProjection;
import com.nbh.edushare.modules.knowledge.pojo.Knowledge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {
    @Query("SELECT k FROM Knowledge k " +
            "LEFT JOIN FETCH k.category " +
            "LEFT JOIN FETCH k.owner " +
            "WHERE k.id = :id")
    Optional<Knowledge> findDetailById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Knowledge k SET k.viewsCount = k.viewsCount + :delta WHERE k.id = :id and k.deletedAt IS NULL")
    int adjustCounters(@Param("id") Long id, @Param("delta") int delta);

    @Modifying
    @Query("""
    UPDATE Knowledge k
    SET k.viewsCount = k.viewsCount + :views,
        k.voteScore = k.voteScore + :votes,
        k.commentCount = k.commentCount + :comments
    WHERE k.id = :id AND k.deletedAt IS NULL
    """)
    int adjustCounters(@Param("id") long id,
                       @Param("views") int views,
                       @Param("votes") int votes,
                       @Param("comments") int comments);

    Page<Knowledge> findByOwnerIdAndDeletedAtIsNull(Long ownerId, Pageable pageable);
}