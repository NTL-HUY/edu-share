package com.nbh.edushare.modules.user.exception;

import com.nbh.edushare.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Người dùng không tồn tại"),
    USERNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "Tên đăng nhập đã tồn tại"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "Email đã tồn tại"),
    USERNAME_OR_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "Tên đăng nhập hoặc Email đã tồn tại")
    ;
    private final int status;
    private final String message;

}
