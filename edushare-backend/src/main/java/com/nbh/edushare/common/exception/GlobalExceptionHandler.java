package com.nbh.edushare.common.exception;

import com.nbh.edushare.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException e, HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();

        log.warn("AppException occurred at [{}] {}: {}",
                request.getMethod(), request.getRequestURI(), errorCode.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus(),
                errorCode.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e, HttpServletRequest request) {

        log.error("Unhandled Exception occurred at [{}] {}: ",
                request.getMethod(), request.getRequestURI(), e);

        return ResponseEntity.status(500)
                .body(new ErrorResponse(500, "Hệ thống đang gặp sự cố", request.getRequestURI()));
    }




}
