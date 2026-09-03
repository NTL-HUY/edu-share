package com.nbh.edushare.modules.interaction.exception;

import com.nbh.edushare.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum InteractionErrorCode implements ErrorCode {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Bình luận này không tồn tại"),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền xoá bình luận này"),
    COMMENT_NOT_ALLOWED(HttpStatus.FORBIDDEN.value(), "Bài đăng này không cho phép bình luận"),
    REPLY_ROOT_MISMATCH(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy bình luận gốc để reply"),

    INVALID_VOTE_VALUE(HttpStatus.BAD_REQUEST.value(), "Giá trị vote không hợp lệ"),
    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Bạn chưa vote cho nội dung này"),
    ;

    private final int status;
    private final String message;
}