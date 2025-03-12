package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Itineraries;
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


public record ItinerariesCreateRequest(
        @NotNull
        UUID tripId,

        @Size(min = 1)
        List<ItineraryCreateRequest> itineraries
) {
    public Itineraries toItineraries(Trip trip) {
        return new Itineraries(trip.getPeriod(), getItinerary(trip));
    }


    private List<Itinerary> getItinerary(Trip trip) {
        return itineraries.stream().map(i -> Itinerary.create(trip, i.date, i.getItineraryDetails())).toList();
    }


    public record ItineraryCreateRequest(
            @FutureOrPresent
            LocalDate date,
            List<ItineraryDetailCreateRequest> itineraryDetails
    ) {
        private List<ItineraryDetail> getItineraryDetails() {
            if (itineraryDetails == null) {
                return new ArrayList<>();
            }
            return itineraryDetails.stream().map(id -> ItineraryDetail.create(id.price, id.description, id.locationId)).toList();
        }

        public record ItineraryDetailCreateRequest(
                Long price,
                UUID locationId,
                String description
        ) {

        }
    }
}
