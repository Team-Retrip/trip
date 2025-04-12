package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.ItineraryDetail;

import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record ItineraryDetailsUpdateRequest(
        @Size(min = 1) List<ItineraryDetailUpdateRequest> itineraryDetails) {
    public List<ItineraryDetail> to(Itinerary itinerary) {
        return itineraryDetails.stream()
                .map(
                        id ->
                                ItineraryDetail.create(
                                        id.price, id.description, itinerary, id.locationId))
                .toList();
    }

    public record ItineraryDetailUpdateRequest(UUID locationId, Long price, String description) {}
}
