package com.retrip.trip.application.in.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ItineraryFixture {
    public static ItinerariesUpdateRequest updateItineraryRequest(List<LocalDate> dates) {
        return new ItinerariesUpdateRequest(
                dates.stream().map(ItinerariesUpdateRequest.ItineraryUpdateRequest::new).toList());
    }
}
