package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripPeriod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
@Embeddable
public class Itineraries {
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Itinerary> values = new ArrayList<>();

    public Itineraries(Trip trip, TripPeriod period) {
        createRegular(trip, period);
    }

    public Itineraries(Trip trip, TripPeriod period, List<LocalDate> dates) {
        validate(period, dates);
        if (isRegular(period.getDays(), dates.size())) {
            createRegular(trip, period);
        }
        createIrregular(trip, period, dates);
    }

    private void validate(TripPeriod period, List<LocalDate> dates) {
        if (dates.stream().anyMatch(period::isNotInclude)) {
            throw new IllegalArgumentException("일정의 날짜는 여행 기간을 벗어날 수 없습니다.");
        }
    }

    private boolean isRegular(int days, int dates) {
        return days == dates;
    }

    private void createRegular(Trip trip, TripPeriod period) {
        IntStream.rangeClosed(1, period.getDays())
                .forEach(n -> this.values.add(
                        Itinerary.create(trip, n, period.getStart().plusDays(n))
                ));
    }

    private void createIrregular(Trip trip, TripPeriod period, List<LocalDate> dates) {
        dates.forEach(d -> this.values.add(
                Itinerary.create(trip, d.compareTo(period.getStart()) + 1, d))
        );
    }
}
