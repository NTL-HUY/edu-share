package com.nbh.edushare.modules.knowledge.pojo;


import com.nbh.edushare.common.model.SoftDeleteModel;
import com.nbh.edushare.modules.knowledge.enums.KnowledgeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "knowledge")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public abstract class Knowledge extends SoftDeleteModel {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false)
    private KnowledgeType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "abstract")
    private String abstractText;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private UserRef owner;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "allow_comment", nullable = false)
    private Boolean allowComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by", referencedColumnName = "id")
    private UserRef deletedBy;

    @Column(name = "views_count", nullable = false)
    private Integer viewsCount = 0;

    @Column(name = "vote_score", nullable = false)
    private Integer voteScore = 0;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;
}