package com.nbh.edushare.modules.auth.refreshtoken;

public record RefreshTokenRotationResult(Long userId, String newRawToken) {
}