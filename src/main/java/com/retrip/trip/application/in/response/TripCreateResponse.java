package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;

import java.time.LocalDate;
import java.util.ArrayList;
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
        List<ItineraryCreateResponse> itineraries
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
