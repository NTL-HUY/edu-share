package com.nbh.edushare.common.exception;

public record GenericErrorCode(int status, String message) implements ErrorCode {

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String name() {
        return "GENERIC_ERROR";
    }
}