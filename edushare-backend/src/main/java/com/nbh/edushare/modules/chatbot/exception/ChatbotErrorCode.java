package com.nbh.edushare.modules.chatbot.exception;

import com.nbh.edushare.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatbotErrorCode implements ErrorCode
{
    EMPTY_QUERY(HttpStatus.BAD_REQUEST.value(), "Câu hỏi không được để trống"),
    RAG_SERVICE_ERROR(HttpStatus.BAD_GATEWAY.value(), "Lỗi từ dịch vụ RAG"),
    RAG_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value(), "Không thể kết nối tới dịch vụ RAG"),
    RAG_SERVICE_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT.value(),  "Dịch vụ RAG phản hồi quá thời gian chờ");


    private final int status;
    private final String message;
}
