package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.IllegalStateException;

public class InvitationRejectNotAllowedException extends IllegalStateException {
    private static final ErrorCode errorCode = ErrorCode.INVITATION_REJECT_NOT_ALLOWED;

    public InvitationRejectNotAllowedException() {
        super(errorCode);
    }
}
