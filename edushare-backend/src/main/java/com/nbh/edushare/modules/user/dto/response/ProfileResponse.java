package com.nbh.edushare.modules.user.dto.response;

import java.math.BigDecimal;

public record ProfileResponse(
        Long userId,
        String username,
        String email,
        String fullName,
        String avatarUrl,
        Boolean isFamous,
        String studentId,
        String university,
        String faculty,
        String major,
        String className,
        String academicYear,
        BigDecimal cpa,
        String bio,
        String coverUrl,
        Boolean isMe,
        Boolean isFollowing
) {

}
