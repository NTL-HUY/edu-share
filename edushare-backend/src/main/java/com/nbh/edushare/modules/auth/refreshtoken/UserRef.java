package com.nbh.edushare.modules.auth.refreshtoken;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

@Entity(name = "AuthUserRef")
@Table(name = "users")
@Immutable
@Getter
@SQLRestriction("deleted_at IS NULL")
class UserRef {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;
}
