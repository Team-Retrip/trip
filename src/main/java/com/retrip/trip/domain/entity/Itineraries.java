package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripPeriod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Itineraries {
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Itinerary> itineraries = new ArrayList<>();

    public Itineraries(Trip trip, TripPeriod period) {
        int days = period.getDays();
        IntStream.rangeClosed(1, days).forEach(n -> this.itineraries.add(Itinerary.create(trip, n)));
    }
}
