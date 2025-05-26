package com.retrip.trip.application.in.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record ItinerariesCreateRequest(
        @Size(min = 1) List<ItineraryCreateRequest> itineraries
) {
  public List<LocalDate> getDates() {
    return itineraries.stream().map(i -> i.date).toList();
  }

  public record ItineraryCreateRequest(@FutureOrPresent LocalDate date) {}
}
