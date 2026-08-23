package com.nbh.edushare.modules.feed.exception;

import com.nbh.edushare.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum FeedErrorCode implements ErrorCode {
    INVALID_CURSOR(HttpStatus.BAD_REQUEST.value(), "Cursor không hợp lệ"),
    ;


    private final int status;
    private final String message;
}
