package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PeriodUpdateResponse(
        UUID tripId, LocalDate start, LocalDate end, List<ItineraryUpdateResponse> itineraries) {

    private static List<ItineraryUpdateResponse> toList(Itineraries itineraries) {
        return itineraries.getValues().stream()
                .map(i -> new ItineraryUpdateResponse(i.getId(), i.getName(), i.getDate()))
                .toList();
    }

    public static PeriodUpdateResponse of(Trip trip) {
        return new PeriodUpdateResponse(
                trip.getId(), trip.getPeriod().getStart(), trip.getPeriod().getEnd(),
                toList(trip.getItineraries())
        );
    }

    public record ItineraryUpdateResponse(UUID id, String name, LocalDate date) {
    }
}
