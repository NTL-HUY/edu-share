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

    @Column(name = "is_resolved", nullable = false)
    private Boolean isResolved;

    @Column(name = "accepted_answer_id")
    private Long acceptedAnswerId;
}
