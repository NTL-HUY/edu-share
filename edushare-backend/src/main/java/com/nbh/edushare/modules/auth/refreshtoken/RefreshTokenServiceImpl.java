package com.nbh.edushare.modules.auth.refreshtoken;

import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.auth.exception.AuthErrorCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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


    @Override
    public String generateRefreshToken(Long userId) {
        String rawToken = generateRefreshTokenStr();
        String tokenHash = hashRefreshTokenStr(rawToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiresAt(Instant.now().plus(Duration.ofDays(refreshTokenTtlDays)));

        UserRef userRef = entityManager.getReference(UserRef.class, userId);
        refreshToken.setUser(userRef);


        refreshTokenRepository.save(refreshToken);
        return rawToken;
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
