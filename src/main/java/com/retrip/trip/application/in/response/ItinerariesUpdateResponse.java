package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Itinerary;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record ItinerariesUpdateResponse(List<ItineraryUpdateResponse> itineraries) {

    public static ItinerariesUpdateResponse of(List<Itinerary> itineraries) {
        return new ItinerariesUpdateResponse(
                itineraries.stream()
                        .map(ItineraryUpdateResponse::of)
                        .sorted(Comparator.comparing(i -> i.date))
                        .toList());
    }

    public record ItineraryUpdateResponse(UUID id, String name, LocalDate date) {

        public static ItineraryUpdateResponse of(Itinerary itinerary) {
            return new ItineraryUpdateResponse(
                    itinerary.getId(), itinerary.getName(), itinerary.getDate());
        }
    }
}
