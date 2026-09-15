package com.nbh.edushare.modules.knowledge.exception;

import com.nbh.edushare.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum KnowledgeErrorCode implements ErrorCode {
    KNOWLEDGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Bài đăng kiến thức này không tồn tại"),
    KNOWLEDGE_ALREADY_DELETED(HttpStatus.BAD_REQUEST.value(), "Bài đăng này đã bị xóa"),
    KNOWLEDGE_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền thao tác trên nội dung này"),
    QUESTION_CONTENT_LOCKED(HttpStatus.FORBIDDEN.value(), "Câu hỏi đã có câu trả lời được chấp nhận, không thể sửa tiêu đề hoặc nội dung"),
    ACCEPTED_ANSWER_INVALID(HttpStatus.BAD_REQUEST.value(), "Bình luận được chọn không tồn tại hoặc không thuộc câu hỏi này"),

    ;
    private final int status;
    private final String message;
}
