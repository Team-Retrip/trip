package com.retrip.trip.application.in.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ItinerariesUpdateRequest(
        @NotNull
        UUID tripId,
        @NotNull
        UUID updateId,

        @Size(min = 1)
        List<ItineraryUpdateRequest> itineraries
) {
    public List<LocalDate> getDates() {
        return itineraries.stream().map(i -> i.date).toList();
    }

    public record ItineraryUpdateRequest(
            @FutureOrPresent
            LocalDate date
    ) {

    }
}
