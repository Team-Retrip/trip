package com.retrip.trip.domain.vo;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParticipantRole {

    LEADER("LEADER", "리더"),
    PARTICIPANT("PARTICIPANT", "참가자");

    private final String code;
    private final String viewName;

    public static ParticipantRole codeOf(String code) {
        return Arrays.stream(ParticipantRole.values())
                .filter(participantRole -> participantRole.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드입니다."));
    }

    public boolean isLeader() {
        return this == LEADER;
    }
}
