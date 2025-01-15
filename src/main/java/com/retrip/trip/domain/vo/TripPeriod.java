package com.retrip.trip.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

import static lombok.AccessLevel.PROTECTED;

@Getter
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor(access = PROTECTED)
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
        if (start.isBefore(LocalDate.now())) {
            throw new RuntimeException();
        }

        if (end.isBefore(start)) {
            throw new RuntimeException();
        }
    }

    public int getDays() {
        return Period.between(start, end).getDays() + 1;
    }
}
