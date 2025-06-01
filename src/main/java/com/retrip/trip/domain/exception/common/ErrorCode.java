package com.retrip.trip.domain.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "Common-001", "Server error"),
    INVALID_INPUT_VALUE(BAD_REQUEST, "Common-002", "Invalid input value"),
    HANDLE_ACCESS_DENIED(FORBIDDEN, "Common-003", "Access is denied"),
    ENTITY_NOT_FOUND(BAD_REQUEST, "Common-004", "Entity not found"),
    ILLEGAL_STATE(BAD_REQUEST, "Common-005", "Illegal state"),

    TRIP_NOT_FOUND(BAD_REQUEST, "Trip-001", "트립 엔티티를 찾을 수 없습니다."),
    PERIOD_UPDATE_FAIL(INTERNAL_SERVER_ERROR, "Trip-002", "여행 일정을 변경할 수 없습니다."),
    TRIP_FULL(BAD_REQUEST, "Trip-003", "여행 참가 인원이 가득 찼습니다."),
    INVALID_MAX_PARTICIPANTS(BAD_REQUEST, "Trip-004", "최대 참여 인원 변경이 불가능합니다."),
    LEADER_REQUIRED(BAD_REQUEST, "Trip-005", "여행 리더 권한이 필요합니다."),
    NOT_RECRUITING(BAD_REQUEST, "Trip-006", "모집 중인 여행이 아닙니다.");
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
