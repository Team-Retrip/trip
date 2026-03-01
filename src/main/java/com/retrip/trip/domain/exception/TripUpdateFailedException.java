package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.ErrorCode;

public class TripUpdateFailedException extends BusinessException {
    private static final ErrorCode errorCode = ErrorCode.TRIP_UPDATE_FAIL;

    public TripUpdateFailedException() {
        super(errorCode);
    }
}
