package com.retrip.trip.domain.vo;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TripStatus {

    RECRUITING("RECRUITING", "모집 중"),
    RECRUITMENT_CLOSED("RECRUITMENT_CLOSED", "모집 완료"),
    BEFORE_TRIP("BEFORE_TRIP", "여행 전"),
    IN_PROGRESS("IN_PROGRESS", "여행 중"),
    COMPLETED("COMPLETED", "여행 후")
    ;

    private final String code;
    private final String viewName;

    public static TripStatus codeOf(String code) {
        return Arrays.stream(TripStatus.values())
                .filter(tripStatus -> tripStatus.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드입니다."));
    }
}
