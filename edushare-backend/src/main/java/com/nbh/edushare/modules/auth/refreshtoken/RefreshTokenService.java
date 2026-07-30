package com.nbh.edushare.modules.auth.refreshtoken;

public interface RefreshTokenService {
    String generateRefreshToken(Long userId);
}
