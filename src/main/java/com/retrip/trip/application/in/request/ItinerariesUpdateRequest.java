package com.retrip.trip.application.in.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record ItinerariesUpdateRequest(@Size(min = 1) List<ItineraryUpdateRequest> itineraries) {

    public List<LocalDate> toDates() {
        return this.itineraries.stream().map(i -> i.date).toList();
    }

    public record ItineraryUpdateRequest(@FutureOrPresent LocalDate date) {}
}
