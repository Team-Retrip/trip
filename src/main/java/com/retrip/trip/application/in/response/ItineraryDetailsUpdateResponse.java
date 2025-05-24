package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.ItineraryDetail;

import java.time.LocalDateTime;
import java.util.UUID;

public record ItineraryDetailsUpdateResponse(
        UUID id,
        LocalDateTime time,
        String description,
        Long price,
        UUID locationId
) {
    public static ItineraryDetailsUpdateResponse of(ItineraryDetail itineraryDetail) {
        return new ItineraryDetailsUpdateResponse(
                itineraryDetail.getId(),
                itineraryDetail.getTime().getValue(),
                itineraryDetail.getDescription().getValue(),
                itineraryDetail.getPrice().getValue(),
                itineraryDetail.getLocationId()
        );
    }
}
