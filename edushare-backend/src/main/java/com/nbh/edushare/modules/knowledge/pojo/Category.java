package com.nbh.edushare.modules.knowledge.pojo;

import com.nbh.edushare.common.model.BaseModel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseModel {
    @Column(name = "name", nullable = false)
    private String name;
}
