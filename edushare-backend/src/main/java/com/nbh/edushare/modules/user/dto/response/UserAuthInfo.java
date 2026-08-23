package com.nbh.edushare.modules.user.dto.response;

import com.nbh.edushare.modules.user.enums.UserRole;

import java.time.LocalDateTime;

public interface UserAuthInfo extends UserBaseProjection {
    String getPasswordHash();
    UserRole getUserRole();
    LocalDateTime getDeletedAt();
    Boolean getIsFamous();
}
