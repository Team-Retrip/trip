package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Itineraries;

import java.time.LocalDate;
import java.util.Comparator;
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
                        .map(i -> new ItineraryUpdateResponse(i.getId(), i.getName(), i.getDate(),
                                i.getItineraryDetails().getValues().stream()
                                        .map(id -> new ItineraryUpdateResponse.ItineraryUpdateDetailResponse(id.getId(), id.getDescription(), id.getPrice(), id.getLocationId()))
                                        .toList()
                        ))
                        .sorted(Comparator.comparing(i -> i.date))
                        .toList()
        );
    }

    public record ItineraryUpdateResponse(
            UUID id,
            String name,
            LocalDate date,
            List<ItineraryUpdateDetailResponse> itineraryDetails
    ) {
        private record ItineraryUpdateDetailResponse(
                UUID id,
                String description,
                Long price,
                UUID locationId
        ) {
        }
    }
}
