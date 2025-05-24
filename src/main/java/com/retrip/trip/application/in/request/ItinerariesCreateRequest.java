package com.retrip.trip.application.in.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ItinerariesCreateRequest(@Size(min = 1) List<ItineraryCreateRequest> itineraries) {
  public List<LocalDate> getDates() {
    return itineraries.stream().map(i -> i.date).toList();
  }

  public record ItineraryCreateRequest(@FutureOrPresent LocalDate date) {}
}
