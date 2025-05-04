package com.retrip.trip.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.temporal.ChronoUnit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;

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
            throw new IllegalArgumentException("여행 시작 일자는 현재보다 이전일 수 없습니다.");
        }

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("여행 종료 일자는 현재보다 이전일 수 없습니다.");
        }

        if (ChronoUnit.DAYS.between(start, end) > 30) {
            throw new IllegalArgumentException("여행 일정은 30일 초과 등록할 수 없습니다.");
        }
    }

    public int getDays() {
        return Math.toIntExact(ChronoUnit.DAYS.between(start, end) + 1);
    }

    public boolean isNotInclude(LocalDate date) {
        return date.isBefore(start) || date.isAfter(end);
    }
}
