package com.retrip.trip.domain.exception;

import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.ErrorCode;

public class PeriodUpdateFailedException extends BusinessException {
    private static final ErrorCode errorCode = ErrorCode.PERIOD_UPDATE_FAIL;

    public PeriodUpdateFailedException() {
        super(errorCode);
    }
}
