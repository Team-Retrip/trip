package com.retrip.trip.domain.vo;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfirmationStatus {
    PENDING, ACCEPTED, REJECTED
}
