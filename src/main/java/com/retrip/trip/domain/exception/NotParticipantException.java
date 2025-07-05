package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.ErrorCode;

public class NotParticipantException extends BusinessException {
    private static final ErrorCode errorCode = ErrorCode.NOT_PARTICIPANT;

    public NotParticipantException(String message) {
        super(errorCode, message);
    }
}