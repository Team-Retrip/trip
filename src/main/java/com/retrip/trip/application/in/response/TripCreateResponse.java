package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TripCreateResponse(
        UUID id,
        UUID leaderId,
        String title,
        UUID destinationId,
        LocalDate start,
        LocalDate end,
        boolean open,
        List<ItineraryResponse> itineraries
) {
    public static TripCreateResponse of(Trip trip) {
        return new TripCreateResponse(
                trip.getId(),
                trip.getLeaderId(),
                trip.getTitle(),
                trip.getDestinationId(),
                trip.getPeriod().getStart(),
                trip.getPeriod().getEnd(),
                trip.isOpen(),
                trip.getItineraries().getItineraries().stream()
                        .map(i -> new ItineraryResponse(i.getId(), i.getName()))
                        .toList()
        );
    }

    private record ItineraryResponse(
            UUID id,
            String name
    ) {
    }
}
