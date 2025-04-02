package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.entity.Itinerary;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record ItinerariesUpdateResponse(UUID tripId, List<ItineraryUpdateResponse> itineraries) {

    public static ItinerariesUpdateResponse of(UUID tripId, Itineraries itineraries) {
        return new ItinerariesUpdateResponse(tripId, toItineraries(itineraries));
    }

    private static List<ItineraryUpdateResponse> toItineraries(Itineraries itineraries) {
        return itineraries.getValues().stream()
                .map(
                        i ->
                                new ItineraryUpdateResponse(
                                        i.getId(), i.getName(), i.getDate(), toItineraryDetails(i)))
                .sorted(Comparator.comparing(i -> i.date))
                .toList();
    }

    private static List<ItineraryUpdateResponse.ItineraryUpdateDetailResponse> toItineraryDetails(
            Itinerary i) {
        return i.getItineraryDetails().getValues().stream()
                .map(
                        id ->
                                new ItineraryUpdateResponse.ItineraryUpdateDetailResponse(
                                        id.getId(),
                                        id.getDescription().getValue(),
                                        id.getPrice().getValue(),
                                        id.getLocationId()))
                .toList();
    }

    public record ItineraryUpdateResponse(
            UUID id,
            String name,
            LocalDate date,
            List<ItineraryUpdateDetailResponse> itineraryDetails) {

        private record ItineraryUpdateDetailResponse(
                UUID id, String description, Long price, UUID locationId) {}
    }
}
