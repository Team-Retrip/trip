package com.retrip.trip.application.in.request;

import java.time.LocalDate;
import java.util.List;

public class ItineraryFixture {
    public static ItinerariesUpdateRequest updateItineraryRequest(List<LocalDate> dates) {
        return new ItinerariesUpdateRequest(
                dates.stream().map(ItinerariesUpdateRequest.ItineraryUpdateRequest::new).toList());
    }

    public static ItineraryDetailsUpdateRequest updateItineraryDetailsRequest(
            List<ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest> itineraryDetails) {
        return new ItineraryDetailsUpdateRequest(itineraryDetails);
    }
}
