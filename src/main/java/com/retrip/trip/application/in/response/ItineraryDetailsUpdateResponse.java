package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.ItineraryDetail;

import java.util.List;
import java.util.UUID;

public record ItineraryDetailsUpdateResponse(
        UUID itineraryId, List<ItineraryDetailUpdateResponse> itineraryDetails) {

    public static ItineraryDetailsUpdateResponse of(Itinerary itinerary) {
        return new ItineraryDetailsUpdateResponse(
                itinerary.getId(),
                itinerary.getItineraryDetails().getValues().stream()
                        .map(
                                id ->
                                        new ItineraryDetailUpdateResponse(
                                                id.getId(),
                                                id.getLocationId(),
                                                id.getPrice().getValue(),
                                                id.getDescription().getValue()))
                        .toList());
    }

    public record ItineraryDetailUpdateResponse(
            UUID id, UUID locationId, Long price, String description) {}
}
