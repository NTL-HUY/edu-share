package com.nbh.edushare.modules.auth.refreshtoken;

public interface RefreshTokenService {
    String generateRefreshToken(Long userId);
    RefreshTokenRotationResult rotateRefreshToken(String rawOldToken);
    void logout(Long userId, String rawToken);
}
