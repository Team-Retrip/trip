package com.retrip.trip.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.temporal.ChronoUnit;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class ItineraryDetailTime {

    @Column(name = "time", nullable = false)
    private LocalDateTime value;

    public ItineraryDetailTime(LocalDateTime time) {
        LocalDateTime value = time.truncatedTo(ChronoUnit.HOURS);
        validate(value);
        this.value = value;
    }

    private void validate(LocalDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException("여행 상세 시간은 필수입니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ItineraryDetailTime that = (ItineraryDetailTime) o;
        return value.getYear() == that.getValue().getYear()
                && value.getMonthValue() == that.getValue().getMonthValue()
                && value.getDayOfMonth() == that.getValue().getDayOfMonth()
                && value.getHour() == that.getValue().getHour();
    }

    public LocalDateTime getValue() {
        return value.truncatedTo(ChronoUnit.HOURS);
    }
}
