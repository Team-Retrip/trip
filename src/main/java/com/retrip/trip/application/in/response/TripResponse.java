package com.retrip.trip.application.in.response;

import java.time.LocalDate;
import java.util.UUID;

public record TripResponse(
        UUID id,
        UUID leaderId,
        String title,
        UUID destinationId,
        LocalDate start,
        LocalDate end,
        boolean open
) {
}
