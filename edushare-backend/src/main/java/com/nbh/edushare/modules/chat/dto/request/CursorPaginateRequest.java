package com.nbh.edushare.modules.chat.dto.request;

public record CursorPaginateRequest(
    Long beforeId,
    Integer limit
) {
}
