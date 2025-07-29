package com.retrip.trip.domain.vo;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParticipantStatus {
    ACTIVE,
    EXPELLED;

    public boolean isExpelled() {
        return this == EXPELLED;
    }
}
