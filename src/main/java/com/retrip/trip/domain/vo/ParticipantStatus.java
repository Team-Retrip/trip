package com.retrip.trip.domain.vo;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParticipantStatus {
    PENDING("PENDING", "대기"),
    APPROVED("APPROVED", "승인"),
    REJECTED("REJECTED", "거절"),
    CANCELED("CANCELED", "취소");

    private final String code;
    private final String viewName;

    public static ParticipantStatus codeOf(String code) {
        return Arrays.stream(ParticipantStatus.values())
                .filter(participantStatus -> participantStatus.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드입니다."));
    }
}
