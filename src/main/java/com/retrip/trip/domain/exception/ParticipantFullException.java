package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.ErrorCode;

public class ParticipantFullException extends BusinessException {
    private static final ErrorCode errorCode = ErrorCode.PARTICIPANT_FULL;

    public ParticipantFullException() {
        super(errorCode);
    }

    public ParticipantFullException(String message) {
        super(errorCode, message);
    }
}
