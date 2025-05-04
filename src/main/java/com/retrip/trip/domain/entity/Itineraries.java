package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripPeriod;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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

    public List<UUID> ids() {
        return this.values.stream().map(Itinerary::getId).collect(Collectors.toList());
    }

    public void updateByPeriod(TripPeriod period, Trip trip) {
        List<Itinerary> containItineraries = getContainItineraries(period);
        List<Itinerary> updateItineraries = updateRegular(trip, period, containItineraries);
        this.values.clear();
        this.values.addAll(updateItineraries);
    }

    private List<Itinerary> getContainItineraries(TripPeriod period) {
        List<Itinerary> result = new ArrayList<>();
        this.values.forEach(itinerary -> {
            if (!period.isNotInclude(itinerary.getDate())) {
                result.add(itinerary);
            } else {
                itinerary.remove();
            }
        });
        result.sort(Comparator.comparing(Itinerary::getDate));
        return result;
    }

    private List<Itinerary> updateRegular(Trip trip, TripPeriod period, List<Itinerary> itineraries) {
        List<Itinerary> updatedItineraries = new ArrayList<>();
        List<LocalDate> dates = itineraries.stream().map(Itinerary::getDate).sorted().toList();
        AtomicInteger idx = new AtomicInteger();
        IntStream.rangeClosed(1, period.getDays())
                .forEach(n -> {
                    LocalDate date = period.getStart().plusDays(n - 1);
                    if (dates.contains(date)) {
                        Itinerary itinerary = itineraries.get(idx.getAndIncrement());
                        itinerary.updateDate(n);
                        updatedItineraries.add(itinerary);
                    } else {
                        updatedItineraries.add(Itinerary.create(trip, n, date));
                    }
                });
        return updatedItineraries;
    }

}
