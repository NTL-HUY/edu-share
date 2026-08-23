package com.nbh.edushare.modules.user.dto.response;

public interface UserBaseProjection {
    Long getId();
    String getUsername();
    String getAvatarUrl();
    String getFullName();
}
