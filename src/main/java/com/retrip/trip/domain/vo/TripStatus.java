package com.retrip.trip.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TripStatus {

    RECRUITING("RECRUITING", "모집중"),
    RECRUITMENT_CLOSED("RECRUITMENT_CLOSED", "모집완료"),
    IN_PROGRESS("IN_PROGRESS", "여행중"),
    COMPLETED("COMPLETED", "여행후")
    ;

    private final String code;
    private final String viewName;

    public static TripStatus codeOf(String code) {
        return Arrays.stream(TripStatus.values())
                .filter(tripStatus -> tripStatus.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드입니다."));
    }

    public boolean cannotCreateInvitations() {
        return this != RECRUITING
                && this != RECRUITMENT_CLOSED;
    }

    public boolean canLeave() {
        return this == RECRUITING || this == RECRUITMENT_CLOSED;
    }

    public boolean canDelegateLeader() {
        return this == RECRUITING || this == RECRUITMENT_CLOSED;
    }
}
