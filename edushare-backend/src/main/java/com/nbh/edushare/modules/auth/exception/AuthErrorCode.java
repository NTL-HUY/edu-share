package com.nbh.edushare.modules.auth.exception;

import com.nbh.edushare.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    JWT_SECRET_KEY_INVALID(500, "Invalid JWT secret key configuration: %s"),
    JWT_TOKEN_EXPIRED(401, "JWT token has expired"),
    JWT_TOKEN_INVALID(401, "Invalid JWT token format or signature: %s"),
    JWT_SIGNING_FAILED(500, "Failed to sign JWT token: %s"),
    INVALID_CREDENTIALS(401, "Invalid username/email or password"),

    TOKEN_HASHING_FAILED(500, "Failed to hash token due to internal security provider issue: %s"),
    ;

    private final int status;
    private final String message;

}
