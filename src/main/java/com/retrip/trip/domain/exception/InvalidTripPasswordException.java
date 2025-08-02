package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.InvalidValueException;

public class InvalidTripPasswordException extends InvalidValueException {
    private static final ErrorCode errorCode = ErrorCode.TRIP_PASSWORD_INVALID;

    public InvalidTripPasswordException() {
        super(errorCode);
    }

    public InvalidTripPasswordException(String message) {
        super(errorCode, message);
    }
}
