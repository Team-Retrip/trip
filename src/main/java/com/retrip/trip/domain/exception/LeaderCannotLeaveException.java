package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.ErrorCode;

public class LeaderCannotLeaveException extends BusinessException {
    private static final ErrorCode errorCode = ErrorCode.LEADER_CANNOT_LEAVE;

    public LeaderCannotLeaveException() {
        super(errorCode);
    }
}