package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.InvalidValueException;

public class NotLeaderException extends InvalidValueException {
    private static final ErrorCode errorCode = ErrorCode.MEMBER_IS_NOT_LEADER;

    public NotLeaderException() {
        super(errorCode);
    }
}
