package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripPeriod;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record TripCreateRequest(
        @NotNull
        UUID memberId,

        @Size(min = 1, max = 30)
        String title,

        @NotNull
        UUID locationId,

        @FutureOrPresent
        LocalDate start,

        @FutureOrPresent
        LocalDate end,
        boolean open
) {
    public Trip to() {
        return Trip.createWithItinerary(title, locationId, new TripPeriod(start, end), open);
    }
}
