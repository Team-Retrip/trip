package com.retrip.trip.application.in.response;

import java.util.UUID;

public record ItineraryDetailDeleteResponse(UUID id) {

    public static ItineraryDetailDeleteResponse of(UUID id) {
        return new ItineraryDetailDeleteResponse(id);
    }
}
