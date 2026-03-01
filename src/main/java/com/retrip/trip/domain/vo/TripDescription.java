package com.retrip.trip.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class TripDescription {
    private static final int LENGTH_LIMIT = 250;

    @Column(name = "description", nullable = false, length = LENGTH_LIMIT)
    private final String value;

    public TripDescription(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value.length() > LENGTH_LIMIT) {
            throw new IllegalArgumentException("여행 소개글은 " + LENGTH_LIMIT + "자를 넘을 수 없습니다.");
        }
    }
}
