package com.retrip.trip.domain.exception.common;

public class EntityNotFoundException extends BusinessException {
    private static final ErrorCode errorCode = ErrorCode.ENTITY_NOT_FOUND;

    public EntityNotFoundException() {
        super(errorCode);
    }

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EntityNotFoundException(String message) {
        super(errorCode, message);
    }

    public EntityNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
