package com.nbh.edushare.modules.auth.security;

import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;

public record TokenPayload(
        String userId,
        String role
) {
    public static TokenPayload from(AuthenticatedUser user) {
        return new TokenPayload(
                String.valueOf(user.getUserAuthInfo().id()),
                user.getUserAuthInfo().role()
        );
    }

    public static TokenPayload from(UserAuthInfo user) {
        return new TokenPayload(
                String.valueOf(user.id()),
                user.role()
        );
    }
}