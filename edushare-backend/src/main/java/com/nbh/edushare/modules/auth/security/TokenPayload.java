package com.nbh.edushare.modules.auth.security;

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
}