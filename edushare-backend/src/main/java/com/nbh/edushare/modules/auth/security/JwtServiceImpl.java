package com.nbh.edushare.modules.auth.security;

import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.auth.dto.response.AccessTokenResponse;
import com.nbh.edushare.modules.auth.exception.AuthErrorCode;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${token.jwt.secret-key}")
    private String secretKey;

    @Value("${token.access-token.ttl.seconds}")
    private long accessTokenTtlSeconds;

    private long accessTokenTtlMillis;
    private JWSSigner jwtSigner;
    private JWSVerifier jwtVerifier;

    @PostConstruct
    public void init() {
        try {
            this.accessTokenTtlMillis = this.accessTokenTtlSeconds * 1000;
            this.jwtSigner = new MACSigner(secretKey);
            this.jwtVerifier = new MACVerifier(secretKey);
        } catch (JOSEException e) {
            // secretKey ngắn hơn 32 ký tự (HS256 yêu cầu tối thiểu 256-bit)
            throw new AppException(AuthErrorCode.JWT_TOKEN_INVALID,e.getMessage());
        }
    }

    @Override
    public AccessTokenResponse generateAccessToken(TokenPayload payload) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenTtlMillis);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(payload.userId())
                .claim("role", payload.role())
                .issueTime(now)
                .expirationTime(exp)
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );

        try {
            signedJWT.sign(jwtSigner);
        } catch (JOSEException e) {
            throw new AppException(AuthErrorCode.JWT_SIGNING_FAILED,e.getMessage());
        }

        return new AccessTokenResponse(signedJWT.serialize(), exp.getTime());
    }

    @Override
    public TokenPayload verifyAndParseAccessToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(jwtVerifier)) {
                throw new AppException(AuthErrorCode.JWT_TOKEN_INVALID, "Invalid token signature");
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            if (claims.getExpirationTime().before(new Date())) {
                throw new AppException(AuthErrorCode.JWT_TOKEN_EXPIRED);
            }

            return new TokenPayload(
                    claims.getSubject(),
                    claims.getStringClaim("role")
            );

        } catch (ParseException e) {
            throw new AppException(AuthErrorCode.JWT_TOKEN_INVALID, "Malformed token");
        } catch (JOSEException e) {
            log.error("JWT verification failed due to JOSE error", e);
            throw new AppException(AuthErrorCode.JWT_TOKEN_INVALID, "Token verification failed");
        }
    }

    @Override
    public Long extractUserId(String token) {
        return Long.parseLong(this.verifyAndParseAccessToken(token).userId());
    }
}
