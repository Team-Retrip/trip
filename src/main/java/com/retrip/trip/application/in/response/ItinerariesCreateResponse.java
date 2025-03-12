package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Itineraries;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record ItinerariesCreateResponse(
        UUID tripId,
        List<ItineraryCreateResponse> itineraries
) {

    public static ItinerariesCreateResponse of(UUID tripId, Itineraries itineraries) {
        return new ItinerariesCreateResponse(
                tripId,
                itineraries.getValues().stream()
                        .map(i -> new ItineraryCreateResponse(i.getId(), i.getName(), i.getDate(),
                                i.getItineraryDetails().getValues().stream()
                                        .map(id -> new ItineraryCreateResponse.ItineraryCreateDetailResponse(
                                                id.getId(), id.getDescription(), id.getPrice(), id.getLocationId()))
                                        .toList()
                        ))
                        .sorted(Comparator.comparing(i -> i.date))
                        .toList()
        );
    }

    public record ItineraryCreateResponse(
            UUID id,
            String name,
            LocalDate date,
            List<ItineraryCreateDetailResponse> itineraryDetails
    ) {
        private record ItineraryCreateDetailResponse(
                UUID id,
                String description,
                Long price,
                UUID locationId
        ) {
        }
    }
}
