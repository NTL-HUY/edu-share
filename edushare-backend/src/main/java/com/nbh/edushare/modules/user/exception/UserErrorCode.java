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
    USERNAME_OR_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "Tên đăng nhập hoặc Email đã tồn tại"),
    USER_INACTIVE(HttpStatus.NOT_FOUND.value(), "Tài khoản đã bị xóa hoặc không còn hoạt động"),


    //Profile
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Không tìm thấy Profile"),
    PROFILE_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "Bạn không được phép sửa profile này"),

    // Follow
    CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST.value(), "Không thể tự follow chính mình"),
    ALREADY_FOLLOWING(HttpStatus.CONFLICT.value(), "Đã follow người dùng này rồi"),
    NOT_FOLLOWING_YET(HttpStatus.BAD_REQUEST.value(), "Chưa follow người dùng này"),

    ;

    private final int status;
    private final String message;

}
