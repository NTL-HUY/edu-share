package com.nbh.edushare.modules.user.dto.response;

import java.math.BigDecimal;

public record ProfileResponse(
        Long userId,
        String studentId,
        String university,
        String faculty,
        String major,
        String className,
        String academicYear,
        BigDecimal cpa,
        String bio,
        String coverUrl
) {
}
