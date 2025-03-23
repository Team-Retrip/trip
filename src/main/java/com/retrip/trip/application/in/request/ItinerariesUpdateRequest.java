package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.ItineraryDetail;
import com.retrip.trip.domain.entity.Trip;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ItinerariesUpdateRequest(
    @NotNull UUID tripId,
    @NotNull UUID updateId,
    @Size(min = 1) List<ItineraryUpdateRequest> itineraries) {

    public List<Itinerary> toItineraries(Trip trip) {
        return itineraries.stream()
            .map(
                i -> {
                    Itinerary itinerary = Itinerary.create(trip, i.date);
                    List<ItineraryDetail> itineraryDetails = i.toItineraryDetails(itinerary);
                    itinerary.createItineraryDetails(itineraryDetails);
                    return itinerary;
                })
            .toList();
    }

    public record ItineraryUpdateRequest(
        @FutureOrPresent LocalDate date, List<ItineraryDetailUpdateRequest> itineraryDetails) {

        private List<ItineraryDetail> toItineraryDetails(Itinerary itinerary) {
            if (itineraryDetails == null || itineraryDetails.isEmpty()) {
                return new ArrayList<>();
            }
            return itineraryDetails.stream()
                .map(id -> ItineraryDetail.create(id.price, id.description, itinerary,
                    id.locationId))
                .toList();
        }

        public record ItineraryDetailUpdateRequest(Long price, UUID locationId,
                                                   String description) {

        }
    }
}
