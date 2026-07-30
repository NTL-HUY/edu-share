package com.nbh.edushare.common.exception;

public interface ErrorCode {
    int getStatus();
    String getMessage();
    String name();
}
