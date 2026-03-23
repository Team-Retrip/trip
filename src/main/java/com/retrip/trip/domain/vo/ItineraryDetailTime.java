package com.retrip.trip.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.temporal.ChronoUnit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class ItineraryDetailTime {

    @Column(name = "time")
    private LocalDateTime value;

    public ItineraryDetailTime(LocalDateTime time) {
        this.value = time != null ? time.truncatedTo(ChronoUnit.HOURS) : null;
    }

    public LocalDateTime getValue() {
        return value != null ? value.truncatedTo(ChronoUnit.HOURS) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItineraryDetailTime that = (ItineraryDetailTime) o;
        if (this.value == null || that.value == null) return false;
        return value.getYear() == that.value.getYear()
                && value.getMonthValue() == that.value.getMonthValue()
                && value.getDayOfMonth() == that.value.getDayOfMonth()
                && value.getHour() == that.value.getHour();
    }

    @Override
    public int hashCode() {
        return value == null ? 0 : getValue().hashCode();
    }
}