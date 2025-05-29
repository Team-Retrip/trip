package com.retrip.trip.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TripInvitationStatus {
    INVITED("INVITED", "초대"),
    ACCEPTED("ACCEPTED", "수락"),
    REJECTED("REJECTED", "거절"),
    EXPIRED("EXPIRED", "만료");

    private final String code;
    private final String viewName;

    public static TripInvitationStatus codeOf(String code) {
        return Arrays.stream(TripInvitationStatus.values())
                .filter(status -> status.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드입니다."));
    }
}
