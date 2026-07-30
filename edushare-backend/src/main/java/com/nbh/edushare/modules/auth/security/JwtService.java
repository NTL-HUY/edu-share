package com.nbh.edushare.modules.auth.security;

import com.nbh.edushare.modules.auth.dto.response.AccessTokenResponse;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.stereotype.Service;

public interface JwtService {
    AccessTokenResponse generateAccessToken(TokenPayload principal);

    TokenPayload verifyAndParseAccessToken(String token);

    Long extractUserId(String token);
}
