package com.nbh.edushare.modules.auth.refreshtoken;

public interface RefreshTokenService {
    GeneratedRefreshToken generateRefreshToken(Long userId);
    RefreshTokenRotationResult rotateRefreshToken(String rawOldToken);
    void logout(Long userId, String rawToken);
}
