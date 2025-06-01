package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.ErrorCode;

public class TripFullException extends BusinessException {
    public TripFullException() {
        super(ErrorCode.TRIP_FULL);
    }
}
