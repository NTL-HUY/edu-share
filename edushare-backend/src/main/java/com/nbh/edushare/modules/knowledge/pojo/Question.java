package com.nbh.edushare.modules.knowledge.pojo;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "question")
@PrimaryKeyJoinColumn(name = "knowledge_id")
@Getter
@Setter
@NoArgsConstructor
public class Question extends Knowledge {

    @Column(name = "content")
    private String content;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id", referencedColumnName = "id")
//    private Category category;

    @Column(name = "is_resolved", nullable = false)
    private Boolean isResolved;

    // accepted_answer_id trỏ sang bảng comment (module khác) → KHÔNG map quan hệ JPA ở đây,
    // chỉ lưu ID thô để tránh phụ thuộc domain module comment.
    @Column(name = "accepted_answer_id")
    private Long acceptedAnswerId;
}
