package com.nbh.edushare.modules.knowledge;


import com.nbh.edushare.common.model.SoftDeleteModel;
import com.nbh.edushare.modules.knowledge.enums.KnowledgeStatus;
import com.nbh.edushare.modules.knowledge.enums.KnowledgeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "knowledge")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor
@SuperBuilder
abstract class Knowledge extends SoftDeleteModel {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false)
    private KnowledgeType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "abstract")
    private String abstractText; // "abstract" là keyword Java, đặt tên khác + @Column ánh xạ lại

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private UserRef owner;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "allow_comment", nullable = false)
    private Boolean allowComment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private KnowledgeStatus status;

    // deleted_by cần một UserRef khác hoặc tái dùng field có sẵn trong SoftDeleteModel
    // (tuỳ bạn đã định nghĩa gì trong common/model/SoftDeleteModel — kiểm tra lại field trùng)

    public void publish() {
        this.status = KnowledgeStatus.PUBLISHED;
    }

    public void archive() {
        this.status = KnowledgeStatus.ARCHIVED;
    }

    // Domain method để service không thao túng field trực tiếp từ ngoài entity
    public void updateBasicInfo(String title, String abstractText, String thumbnailUrl) {
        this.title = title;
        this.abstractText = abstractText;
        this.thumbnailUrl = thumbnailUrl;
    }
}