package com.nbh.edushare.modules.user.dto.response;

import com.nbh.edushare.modules.user.enums.UserRole;

public interface UserRoleProjection {
    Long getId();
    UserRole getUserRole();
}
