package com.nbh.edushare.modules.user.dto.response;

import java.time.LocalDateTime;

public record UserAuthInfo(
        Long id,
        String email,
        String username,
        String passwordHash,
        String role,
        LocalDateTime deletedAt
) {}
