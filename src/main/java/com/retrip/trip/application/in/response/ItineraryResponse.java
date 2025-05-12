package com.retrip.trip.application.in.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ItineraryResponse(
        UUID id,
        LocalDate date,
        String name,
        List<ItineraryDetailResponse> itineraryDetailResponse) {
    public record ItineraryDetailResponse(
            UUID id, String description, LocalDateTime time, Long price, UUID locationId) {
    }
}
