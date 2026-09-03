package com.nbh.edushare.modules.user.dto.response;

import java.math.BigDecimal;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String fullName,
        String avatarUrl,
        Boolean isFamous,
        String userRole,

        // Profile details
        String studentId,
        String university,
        String faculty,
        String major,
        String className,
        String academicYear,
        BigDecimal cpa,
        String bio,
        String coverUrl
) {}