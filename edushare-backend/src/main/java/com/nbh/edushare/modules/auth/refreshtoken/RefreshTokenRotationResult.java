package com.nbh.edushare.modules.auth.refreshtoken;

import java.time.Instant;

public record RefreshTokenRotationResult(Long userId, String newRawToken, Instant expiresAt) {
}