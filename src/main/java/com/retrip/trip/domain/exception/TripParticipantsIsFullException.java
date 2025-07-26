package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.IllegalStateException;

public class TripParticipantsIsFullException extends IllegalStateException {
    private static final ErrorCode errorCode = ErrorCode.TRIP_PARTICIPANTS_IS_FULL;

    public TripParticipantsIsFullException() {
        super(errorCode);
    }
}
