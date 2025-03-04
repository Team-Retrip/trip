package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Itineraries;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ItinerariesUpdateResponse(
        UUID tripId,
        List<ItineraryUpdateResponse> itineraries
) {

    public static ItinerariesUpdateResponse of(UUID tripId, Itineraries itineraries) {
        return new ItinerariesUpdateResponse(
                tripId,
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
