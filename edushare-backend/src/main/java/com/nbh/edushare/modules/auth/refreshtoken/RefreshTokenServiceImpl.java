package com.nbh.edushare.modules.auth.refreshtoken;

import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.auth.dto.response.AuthTokenResponse;
import com.nbh.edushare.modules.auth.exception.AuthErrorCode;
import com.nbh.edushare.modules.auth.security.JwtService;
import com.nbh.edushare.modules.user.exception.UserErrorCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
class RefreshTokenServiceImpl implements RefreshTokenService {
    @Value("${token.refresh-token.ttl.days}")
    private long refreshTokenTtlDays;

    private final RefreshTokenRepository refreshTokenRepository;
    private final EntityManager entityManager;

    private final JwtService jwtService;


    @Override
    public GeneratedRefreshToken generateRefreshToken(Long userId) {
        String rawToken = generateRefreshTokenStr();
        String tokenHash = hashRefreshTokenStr(rawToken);
        Instant expiresAt = Instant.now().plus(Duration.ofDays(refreshTokenTtlDays));
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiresAt(expiresAt);

        UserRef userRef = entityManager.getReference(UserRef.class, userId);
        refreshToken.setUser(userRef);


        refreshTokenRepository.save(refreshToken);
        return new GeneratedRefreshToken(rawToken, expiresAt);
    }

    private RefreshToken  verifyAndGetRefreshToken(String rawToken) {
        String tokenHash = hashRefreshTokenStr(rawToken);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new AppException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (refreshToken.isRevoked()) {
            throw new AppException(AuthErrorCode.REFRESH_TOKEN_REVOKED);
        }

        if (refreshToken.isExpired()) {
            throw new AppException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        try {
            refreshToken.getUser().getId(); // force load, kiểm tra user còn tồn tại (chưa bị filter)
        } catch (EntityNotFoundException e) {
            throw new AppException(UserErrorCode.USER_INACTIVE);
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public RefreshTokenRotationResult rotateRefreshToken(String rawOldToken){
        RefreshToken oldToken = verifyAndGetRefreshToken(rawOldToken);

        if (refreshTokenRepository.revokeTokenById(oldToken.getId()) == 0) {
            throw new AppException(AuthErrorCode.REFRESH_TOKEN_REVOKE_FAILED);
        }
        Long userId = oldToken.getUser().getId();
        GeneratedRefreshToken newToken = generateRefreshToken(userId);

        return new RefreshTokenRotationResult(userId, newToken.rawToken(), newToken.expiresAt());
    }

    @Override
    @Transactional
    public void logout(Long userId, String rawToken) {
        RefreshToken token = verifyAndGetRefreshToken(rawToken);

        if (!token.getUser().getId().equals(userId)) {
            throw new AppException(AuthErrorCode.REFRESH_TOKEN_INVALID);
        }

        if (refreshTokenRepository.revokeTokenById(token.getId()) == 0) {
            throw new AppException(AuthErrorCode.REFRESH_TOKEN_REVOKE_FAILED);
        }
    }

    private String generateRefreshTokenStr() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String hashRefreshTokenStr(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new AppException(AuthErrorCode.TOKEN_HASHING_FAILED, e.getMessage());
        }
    }
}
