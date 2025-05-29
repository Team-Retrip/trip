package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.InvalidValueException;

public class MemberIsNotLeaderException extends InvalidValueException {
    private static final ErrorCode errorCode = ErrorCode.MEMBER_IS_NOT_LEADER;

    public MemberIsNotLeaderException() {
        super(errorCode);
    }
}
