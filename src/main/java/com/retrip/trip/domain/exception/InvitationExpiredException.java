package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.IllegalStateException;

public class InvitationExpiredException extends IllegalStateException {
    private static final ErrorCode errorCode = ErrorCode.INVITATION_EXPIRED;

    public InvitationExpiredException() {
        super(errorCode);
    }
}
