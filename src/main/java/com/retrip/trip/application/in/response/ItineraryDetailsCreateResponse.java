package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.ItineraryDetail;

import java.time.LocalDateTime;
import java.util.UUID;

public record ItineraryDetailsCreateResponse(
        UUID id,
        LocalDateTime time,
        String description,
        Long price,
        UUID locationId
) {
    public static ItineraryDetailsCreateResponse of(ItineraryDetail itineraryDetail) {
        return new ItineraryDetailsCreateResponse(
                itineraryDetail.getId(),
                itineraryDetail.getTime().getValue(),
                itineraryDetail.getDescription().getValue(),
                itineraryDetail.getPrice().getValue(),
                itineraryDetail.getLocationId()
        );
    }
}
