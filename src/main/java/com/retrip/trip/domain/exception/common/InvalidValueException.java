package com.retrip.trip.domain.exception.common;

public class InvalidValueException extends BusinessException {
    private static final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

    public InvalidValueException() {
        super(errorCode);
    }
    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidValueException(String message) {
        super(errorCode, message);
    }

    public InvalidValueException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
