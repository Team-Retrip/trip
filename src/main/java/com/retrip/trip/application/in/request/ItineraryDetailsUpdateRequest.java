package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.ItineraryDetail;

import java.time.LocalDateTime;
import java.util.UUID;

public record ItineraryDetailsUpdateRequest(
        LocalDateTime time,
        String description,
        Long price,
        UUID locationId
) {
    public ItineraryDetail to(Itinerary itinerary) {
        return ItineraryDetail.create(price, description, time, itinerary, locationId);
    }
}
