package com.nbh.edushare.common.exception;

import com.nbh.edushare.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

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

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {

        log.warn("Method [{}] not supported at URL [{}]: {}",
                request.getMethod(), request.getRequestURI(), e.getMessage());

        String message = String.format("Phương thức %s không được hỗ trợ cho đường dẫn này", e.getMethod());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse(405, message, request.getRequestURI()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("Malformed JSON request at [{}]: {}", request.getRequestURI(), e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "Cú pháp JSON không hợp lệ (sai dấu ngoặc hoặc thiếu dấu phẩy)", request.getRequestURI()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        // Lấy thông điệp lỗi đầu tiên từ Annotation (ví dụ: "File không được để trống")
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Dữ liệu đầu vào không hợp lệ");

        log.warn("Validation failed at [{}] {}: {}", request.getMethod(), request.getRequestURI(), errorMessage);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e, HttpServletRequest request) {

        log.error("Unhandled Exception occurred at [{}] {}: ",
                request.getMethod(), request.getRequestURI(), e);

        return ResponseEntity.status(500)
                .body(new ErrorResponse(500, "Hệ thống đang gặp sự cố", request.getRequestURI()));
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("Type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());

        String message = String.format("Tham số '%s' phải là kiểu dữ liệu %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "hợp lệ");

        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
               request.getRequestURI()
        );
    }

}
