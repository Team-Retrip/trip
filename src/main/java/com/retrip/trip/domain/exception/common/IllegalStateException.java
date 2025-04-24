package com.retrip.trip.domain.exception.common;

public class IllegalStateException extends BusinessException {
    private static final ErrorCode errorCode = ErrorCode.ILLEGAL_STATE;

    public IllegalStateException() {
        super(errorCode);
    }

    public IllegalStateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public IllegalStateException(String message) {
        super(errorCode, message);
    }

    public IllegalStateException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
