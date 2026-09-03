package com.nbh.edushare.modules.media;

import com.nbh.edushare.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MediaErrorCode implements ErrorCode {

    UPLOAD_FAILED(400, "Tải file lên hệ thống thất bại"),
    DELETE_FAILED(400, "Xóa file thất bại"),
    FILE_TOO_LARGE(400, "Dung lượng file vượt quá giới hạn cho phép"),
    UNSUPPORTED_FILE_TYPE(400, "Định dạng file không được hỗ trợ"),
    FILE_NOT_FOUND(404, "Không tìm thấy file trong hệ thống"),
    FILE_IS_EMPTY(400,"File hiện đang rỗng")
    ;

    private final int status;
    private final String message;
}