package com.nbh.edushare.modules.interaction.repository;

import com.nbh.edushare.modules.interaction.pojo.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Trang comment gốc của 1 bài, dùng đúng index idx_comment_knowledge_root_created
    @Query("""
            SELECT c FROM Comment c 
            WHERE c.knowledgeId = :knowledgeId 
              AND c.rootCommentId IS NULL 
              AND (c.deletedAt IS NULL OR c.replyCount > 0)
            """)
    Page<Comment> findRootCommentsForFeed(@Param("knowledgeId") Long knowledgeId, Pageable pageable);

    @Query("""
                SELECT c FROM Comment c
                WHERE c.knowledgeId = :knowledgeId
                  AND c.rootCommentId IS NULL
                  AND (c.deletedAt IS NULL OR c.replyCount > 0)
                  AND (
                        CAST(:createdAt AS timestamp) IS NULL
                        OR c.createdAt < :createdAt
                        OR (c.createdAt = :createdAt AND c.id < :id)
                  )
                ORDER BY c.createdAt DESC, c.id DESC
            """)
    List<Comment> findRootCommentsForFeed(
                    @Param("knowledgeId") Long knowledgeId,
                    @Param("createdAt") LocalDateTime createdAt,
                    @Param("id") Long id,
                    Pageable pageable
            );

    // Lấy toàn bộ reply theo 1 root, sắp theo thời gian tạo
    List<Comment> findByKnowledgeIdAndRootCommentIdAndDeletedAtIsNullOrderByCreatedAtAsc(
            Long knowledgeId, Long rootCommentId);

    Optional<Comment> findByIdAndDeletedAtIsNull(Long id);

    @Modifying
    @Query("UPDATE Comment c SET c.replyCount = c.replyCount + :delta WHERE c.id = :rootId")
    void adjustReplyCount(@Param("rootId") Long rootId, @Param("delta") int delta);

}