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
    ILLEGAL_ARGUMENT(BAD_REQUEST, "Common-006", "Illegal argument"),

    TRIP_NOT_FOUND(BAD_REQUEST, "Trip-001", "트립 엔티티를 찾을 수 없습니다."),
    PERIOD_UPDATE_FAIL(INTERNAL_SERVER_ERROR, "Trip-002", "여행 일정을 변경할 수 없습니다."),
    NOT_TRIP_LEADER(BAD_REQUEST, "Trip-003", "여행 리더가 아니면 접근할 수 없습니다."),
    TRIP_INVITATION_DUPLICATE(BAD_REQUEST, "Trip-004", "사용자를 여행에 중복 초대할 수 없습니다."),
    MEMBER_IS_NOT_LEADER(BAD_REQUEST, "Trip-005", "여행 리더가 아니면 접근할 수 없습니다."),
    TRIP_MEMBER_NOT_IN_TRIP(BAD_REQUEST, "TRIP-006", "강퇴 대상 멤버가 해당 여행에 포함되어 있지 않습니다."),
    TRIP_MEMBER_BANNED_CANNOT_APPLY(HttpStatus.BAD_REQUEST, "TRIP-007", "강퇴된 사용자는 해당 여행에 참가 신청할 수 없습니다."),
    LEADER_CANNOT_LEAVE(BAD_REQUEST, "Trip-008", "리더는 여행을 나갈 수 없습니다. 먼저 리더를 위임해야 합니다."),
    TRIP_NOT_READY(BAD_REQUEST, "Trip-009", "여행이 준비 상태일 때만 나갈 수 있습니다."),
    NOT_PARTICIPANT(BAD_REQUEST, "Trip-010", "여행 참여자가 아닙니다."),
    TRIP_FULL(BAD_REQUEST, "Trip-011", "여행 참가 인원이 가득 찼습니다."),
    INVALID_MAX_PARTICIPANTS(BAD_REQUEST, "Trip-012", "최대 참여 인원 변경이 불가능합니다."),
    LEADER_REQUIRED(BAD_REQUEST, "Trip-013", "여행 리더 권한이 필요합니다."),
    NOT_RECRUITING(BAD_REQUEST, "Trip-014", "모집 중인 여행이 아닙니다."),
    INVITATION_EXPIRED(BAD_REQUEST, "Trip-015", "초대가 만료되었습니다."),
    TRIP_NOT_RECRUITING(BAD_REQUEST, "Trip-016", "여행이 모집중이 아닙니다."),
    TRIP_PARTICIPANTS_IS_FULL(BAD_REQUEST, "Trip-017", "여행 참여자가 가득 찼습니다."),
    INVITATION_REJECT_NOT_ALLOWED(BAD_REQUEST, "Trip-018", "초대 거절이 불가능한 상태입니다."),
    TRIP_CONFIRMATION_PERIOD_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "TRIP-019", "요청한 여행 확정 기간은 여행의 전체 기간 범위를 벗어날 수 없습니다."),
    TRIP_CONFIRMATION_START_AFTER_END(HttpStatus.BAD_REQUEST, "TRIP-020", "요청한 여행 확정 시작일은 종료일보다 이후일 수 없습니다."),
    NOT_FOUND_PARTICIPANTS(HttpStatus.BAD_REQUEST, "TRIP-021", "참가자가 존재하지 않습니다."),
    NOT_TRIP_READY_STATUS(HttpStatus.BAD_REQUEST, "TRIP-022", "여행 준비 상태가 아닙니다."),
    TRIP_PASSWORD_INVALID(BAD_REQUEST, "Trip-023", "비공개 여행 비밀번호의 길이가 적절하지 않습니다."),
    TRIP_DEMAND_NOT_ALLOWED(BAD_REQUEST, "Trip-024", "이미 참여를 요청했거나 참여 중인 여행이므로 참여 요청을 다시 할 수 없습니다."),
    TRIP_DEMAND_STATUS_NOT_PENDING(BAD_REQUEST, "Trip-025", "현재 참여 요청 상태가 ‘대기’가 아니므로 해당 요청을 수행할 수 없습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
