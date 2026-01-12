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
    TRIP_MEMBER_NOT_IN_TRIP(BAD_REQUEST, "Trip-006", "강퇴 대상 멤버가 해당 여행에 포함되어 있지 않습니다."),
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
    TRIP_CONFIRMATION_PERIOD_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "Trip-019", "요청한 여행 확정 기간은 여행의 전체 기간 범위를 벗어날 수 없습니다."),
    TRIP_CONFIRMATION_START_AFTER_END(HttpStatus.BAD_REQUEST, "Trip-020", "요청한 여행 확정 시작일은 종료일보다 이후일 수 없습니다."),
    NOT_FOUND_PARTICIPANTS(HttpStatus.BAD_REQUEST, "Trip-021", "참가자가 존재하지 않습니다."),
    NOT_TRIP_READY_STATUS(HttpStatus.BAD_REQUEST, "Trip-022", "여행 준비 상태가 아닙니다."),
    TRIP_PASSWORD_INVALID(BAD_REQUEST, "Trip-023", "비공개 여행 비밀번호의 길이가 적절하지 않습니다."),
    TRIP_DEMAND_NOT_ALLOWED(BAD_REQUEST, "Trip-024", "이미 참여를 요청했거나 참여 중인 여행이므로 참여 요청을 다시 할 수 없습니다."),
    TRIP_DEMAND_STATUS_NOT_PENDING(BAD_REQUEST, "Trip-025", "현재 참여 요청 상태가 ‘대기’가 아니므로 해당 요청을 수행할 수 없습니다."),
    INVALID_MAX_PARTICIPANTS_VALUE(BAD_REQUEST,"Trip-026","최대 참여 인원은 1명 이상이어야 합니다."),
    MAX_PARTICIPANTS_LESS_THAN_CURRENT(BAD_REQUEST,"Trip-027","현재 참여 인원보다 적은 수로 변경할 수 없습니다."),
    INVALID_HASHTAG_LENGTH(BAD_REQUEST, "Trip-028", "HashTag는 1~10자 사이여야 합니다."),
    PRIVATE_TRIP_PASSWORD_REQUIRED(BAD_REQUEST, "Trip-029", "비공개 여행은 비밀번호를 반드시 입력해야 합니다."),
    TRIP_DAY_MUST_BE_POSITIVE(BAD_REQUEST, "Trip-030", "여행 일차는 1보다 작을 수 없습니다."),
    TRIP_START_DATE_IN_PAST(BAD_REQUEST, "Trip-031", "여행 시작 일자는 현재보다 이전일 수 없습니다."),
    TRIP_END_DATE_BEFORE_START(BAD_REQUEST, "Trip-032", "여행 종료 일자는 시작 일자보다 이전일 수 없습니다."),
    TRIP_DURATION_EXCEEDS_LIMIT(BAD_REQUEST, "Trip-033", "여행 일정은 30일을 초과할 수 없습니다."),
    CANNOT_DELEGATE_LEADER_TO_SELF(BAD_REQUEST, "Trip-034", "자기 자신에게 리더를 위임할 수 없습니다."),
    PARTICIPATION_CONFIRM_REQUEST_NOT_FOUND(BAD_REQUEST, "Trip-035", "참여 확정 요청을 찾을 수 없습니다."),
    TARGET_ENTITY_NOT_FOUND(NOT_FOUND, "Trip-036", "작업을 수행할 대상을 찾을 수 없습니다."),
    DEMAND_NOT_FOUND(BAD_REQUEST, "Trip-037", "요청 엔티티를 찾을 수 없습니다."),
    INVITATION_NOT_FOUND(BAD_REQUEST, "Trip-038", "초대장 엔티티를 찾을 수 없습니다."),
    ITINERARY_NOT_FOUND(BAD_REQUEST, "Trip-039", "일정을 찾을 수 없습니다."),
    ITINERARY_DATE_MISMATCH(BAD_REQUEST, "Trip-040", "일정과 상세 일정 일자가 다릅니다."),
    ITINERARY_TIME_DUPLICATED(BAD_REQUEST, "Trip-041", "해당 시간에는 이미 상세 일정이 있습니다."),
    TRIP_PASSWORD_MISMATCH(BAD_REQUEST, "Trip-042", "여행 비밀번호가 일치하지 않습니다."),
    VOTE_MODIFY_FORBIDDEN(FORBIDDEN, "Trip-043", "투표를 만든 사람이 아니면 수정, 종료, 삭제 할 수 없습니다.")
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
