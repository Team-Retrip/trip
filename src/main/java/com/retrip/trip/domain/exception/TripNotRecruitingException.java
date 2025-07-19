package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.IllegalStateException;

public class TripNotRecruitingException extends IllegalStateException {
    private static final ErrorCode errorCode = ErrorCode.INVITATION_EXPIRED;

    public TripNotRecruitingException() {
        super(errorCode);
    }
}
