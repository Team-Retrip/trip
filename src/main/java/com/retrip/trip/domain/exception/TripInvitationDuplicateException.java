package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.IllegalStateException;

public class TripInvitationDuplicateException extends IllegalStateException {
    private static final ErrorCode errorCode = ErrorCode.TRIP_INVITATION_DUPLICATE;

    public TripInvitationDuplicateException() {
        super(errorCode);
    }
}
