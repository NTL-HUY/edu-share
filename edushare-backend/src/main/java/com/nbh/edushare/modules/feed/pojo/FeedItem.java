package com.nbh.edushare.modules.feed.pojo;
import com.fasterxml.jackson.databind.JsonNode;
import com.nbh.edushare.modules.feed.dto.response.FeedCountProjection;
import com.nbh.edushare.modules.knowledge.enums.KnowledgeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "feed_item")
@Getter
@Setter
@NoArgsConstructor
public class FeedItem {

    @Id
    @Column(name = "knowledge_id")
    private Long knowledgeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private KnowledgeType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "abstract")
    private String abstractText;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "owner_avatar_url")
    private String ownerAvatarUrl;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @Column(name = "allow_comment", nullable = false)
    private Boolean allowComment = true;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name")
    String categoryName;

    @Column(name = "views_count", nullable = false)
    private Integer viewsCount;

    @Column(name = "vote_score", nullable = false)
    private Integer voteScore;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "type_meta", columnDefinition = "jsonb")
    private JsonNode typeMeta;

    @Column(name = "source_created_at", nullable = false)
    private LocalDateTime sourceCreatedAt;

    @CreationTimestamp
    @Column(name = "synced_at", nullable = false)
    private LocalDateTime syncedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}