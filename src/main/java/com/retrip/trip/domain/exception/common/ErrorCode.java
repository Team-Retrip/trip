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
    NOT_TRIP_LEADER(BAD_REQUEST, "Trip-003", "여행 리더가 아니면 접근할 수 없습니다."),
    TRIP_INVITATION_DUPLICATE(BAD_REQUEST, "Trip-004", "사용자를 여행에 중복 초대할 수 없습니다."),
    MEMBER_IS_NOT_LEADER(BAD_REQUEST, "Trip-005", "여행 리더가 아니면 접근할 수 없습니다."),
    TRIP_MEMBER_NOT_IN_TRIP(BAD_REQUEST, "TRIP-006","강퇴 대상 멤버가 해당 여행에 포함되어 있지 않습니다."),
    TRIP_MEMBER_BANNED_CANNOT_APPLY(HttpStatus.BAD_REQUEST, "TRIP-007", "강퇴된 사용자는 해당 여행에 참가 신청할 수 없습니다."),
    MEMBER_IS_NOT_LEADER(BAD_REQUEST, "Trip-007", "여행 리더가 아니면 접근할 수 없습니다."),
    LEADER_CANNOT_LEAVE(BAD_REQUEST, "Trip-008", "리더는 여행을 나갈 수 없습니다. 먼저 리더를 위임해야 합니다."),
    TRIP_NOT_READY(BAD_REQUEST, "Trip-009", "여행이 준비 상태일 때만 나갈 수 있습니다."),
    NOT_PARTICIPANT(BAD_REQUEST, "Trip-010", "여행 참여자가 아닙니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
