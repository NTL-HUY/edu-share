package com.nbh.edushare.modules.interaction.pojo;

import com.nbh.edushare.common.model.BaseModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vote")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote extends BaseModel {

    @Column(name = "knowledge_id", nullable = false)
    private Long knowledgeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "value", nullable = false)
    private Short value;

}