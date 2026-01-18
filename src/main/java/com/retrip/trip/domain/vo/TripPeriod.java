package com.retrip.trip.domain.vo;

import com.retrip.trip.domain.exception.common.InvalidValueException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.temporal.ChronoUnit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;

import static com.retrip.trip.domain.exception.common.ErrorCode.*;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED, force = true)
public class TripPeriod {
    @Column(name = "start_date")
    private LocalDate start;
    @Column(name = "end_date")
    private LocalDate end;

    public TripPeriod(LocalDate start, LocalDate end) {
        validate(start, end);
        this.start = start;
        this.end = end;
    }

    private void validate(LocalDate start, LocalDate end) {
        if (start.isBefore(ChronoLocalDate.from(ZonedDateTime.now()))) {
            throw new InvalidValueException(TRIP_START_DATE_IN_PAST);
        }

        if (end.isBefore(start)) {
            throw new InvalidValueException(TRIP_END_DATE_BEFORE_START);
        }

        if (ChronoUnit.DAYS.between(start, end) > 30) {
            throw new InvalidValueException(TRIP_DURATION_EXCEEDS_LIMIT);
        }
    }

    public int getDays() {
        return Math.toIntExact(ChronoUnit.DAYS.between(start, end) + 1);
    }

    public boolean isNotInclude(LocalDate date) {
        return date.isBefore(start) || date.isAfter(end);
    }
}
