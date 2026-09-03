package com.nbh.edushare.modules.knowledge.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.SQLRestriction;

@Entity(name = "KnowledgeUserRef")
@Table(name = "users")
@Immutable
@Getter
@SQLRestriction("deleted_at IS NULL")
public class UserRef {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
}
