package com.retrip.trip.infra.adapter.in.presentation.rest.common;

import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.IllegalStateException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<ErrorResponse> handleAccessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        log.error("handleAccessDeniedException: ", e);
        return handle(ErrorCode.HANDLE_ACCESS_DENIED, request, e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public ApiResponse<ErrorResponse> handleBindException(HttpServletRequest request, BindException e) {
        log.error("handleBindException: ", e);
        return ApiResponse.of(
                ErrorResponse.of(
                        ErrorCode.INVALID_INPUT_VALUE,
                        request.getRequestURL().toString(),
                        request.getMethod(),
                        e.getBindingResult()
                ));
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<ErrorResponse> handleBusinessException(HttpServletRequest request, BusinessException e) {
        log.error("handleBusinessException: ", e);
        return handle(e.getErrorCode(), request, e.getMessage());
    }

    @ExceptionHandler(exception = IllegalArgumentException.class)
    public ApiResponse<ErrorResponse> handleIllegalArgumentException(HttpServletRequest request, Exception e) {
        log.error("handleException: ", e);
        return handle(ErrorCode.ILLEGAL_ARGUMENT, request, e.getMessage());
    }

    @ExceptionHandler(exception = IllegalStateException.class)
    public ApiResponse<ErrorResponse> handleIllegalStateException(HttpServletRequest request, Exception e) {
        log.error("handleException: ", e);
        return handle(ErrorCode.ILLEGAL_STATE, request, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<ErrorResponse> handleException(HttpServletRequest request, Exception e) {
        log.error("handleException: ", e);
        return handle(ErrorCode.SERVER_ERROR, request, e.getMessage());
    }

    private static ApiResponse<ErrorResponse> handle(ErrorCode errorCode, HttpServletRequest request, String errorMessage) {
        return ApiResponse.of(
                ErrorResponse.of(
                        errorCode, request.getRequestURL().toString(), request.getMethod(), errorMessage
                ));
    }
}
