package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record TripCreateResponse(
        UUID id,
        UUID destinationId,
        String title,
        String description,
        LocalDate start,
        LocalDate end,
        boolean open,
        int maxParticipants,
        String category,
        List<ItineraryCreateResponse> itineraries
) {
    public static TripCreateResponse of(Trip trip) {
        return new TripCreateResponse(
                trip.getId(),
                trip.getDestinationId(),
                trip.getTitle().getValue(),
                trip.getDescription().getValue(),
                trip.getPeriod().getStart(),
                trip.getPeriod().getEnd(),
                trip.isOpen(),
                trip.getMaxParticipants(),
                trip.getCategory().getViewName(),
                trip.getItineraries() == null ? new ArrayList<>() :
                        trip.getItineraries().getValues().stream()
                        .map(i -> new ItineraryCreateResponse(i.getId(), i.getName(), i.getDate()))
                        .toList()
        );
    }

    private record ItineraryCreateResponse(
            UUID id,
            String name,
            LocalDate date
    ) {
    }
}
