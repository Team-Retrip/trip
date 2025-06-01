package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.EntityNotFoundException;
import com.retrip.trip.domain.exception.common.ErrorCode;

public class TripNotFoundException extends EntityNotFoundException {
    private static final ErrorCode errorCode = ErrorCode.TRIP_NOT_FOUND;

    public TripNotFoundException() {
        super(errorCode);
    }
}
