package com.nbh.edushare.modules.user;


import com.nbh.edushare.common.model.SoftDeleteModel;
import com.nbh.edushare.modules.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
class User extends SoftDeleteModel {

    // đánh thêm partial unique bằng sql
    @Column(nullable = false,  length = 50)
    private String username;

    @Column(nullable = false,  length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "is_famous", nullable = false)
    private Boolean isFamous = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role",nullable = false)
    private UserRole userRole = UserRole.USER;

//    xử lý thêm cascade của profile bằng sql

}
