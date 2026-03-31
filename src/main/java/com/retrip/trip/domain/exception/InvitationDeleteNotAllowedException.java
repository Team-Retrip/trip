package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.IllegalStateException;

public class InvitationDeleteNotAllowedException extends IllegalStateException {
    private static final ErrorCode errorCode = ErrorCode.INVITATION_CANNOT_DELETE;

    public InvitationDeleteNotAllowedException() {
        super(errorCode);
    }
}
