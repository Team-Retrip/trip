package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.vo.TripPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PeriodUpdateResponse(
        UUID tripId,
        LocalDate start,
        LocalDate end,
        List<ItineraryUpdateResponse> itineraries
) {

    public static PeriodUpdateResponse of(UUID tripId, TripPeriod period, Itineraries itineraries) {
        return new PeriodUpdateResponse(
                tripId,
                period.getStart(),
                period.getEnd(),
                itineraries.getValues().stream()
                        .map(i -> new ItineraryUpdateResponse(i.getId(), i.getName(), i.getDate()))
                        .toList()
        );
    }

    private record ItineraryUpdateResponse(
            UUID id,
            String name,
            LocalDate date
    ) {
    }
}
