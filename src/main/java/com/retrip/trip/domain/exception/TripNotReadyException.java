package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.ErrorCode;

public class TripNotReadyException extends BusinessException {
    private static final ErrorCode errorCode = ErrorCode.TRIP_NOT_READY;

    public TripNotReadyException() {
        super(errorCode);
    }
}