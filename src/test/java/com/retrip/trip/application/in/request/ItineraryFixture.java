package com.retrip.trip.application.in.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ItineraryFixture {


    public static ItinerariesUpdateRequest.ItineraryUpdateRequest.ItineraryDetailUpdateRequest updateItineraryDetailRequest(
            long price,
            UUID locationId,
            String description
    ) {
        return new ItinerariesUpdateRequest.ItineraryUpdateRequest.ItineraryDetailUpdateRequest(price, locationId, description);
    }

    public static ItinerariesUpdateRequest.ItineraryUpdateRequest updateItineraryRequest(
            LocalDate localDate,
            List<ItinerariesUpdateRequest.ItineraryUpdateRequest.ItineraryDetailUpdateRequest> itineraryDetailUpdateRequests
    ) {
        return new ItinerariesUpdateRequest.ItineraryUpdateRequest(localDate, itineraryDetailUpdateRequests);
    }

   
}
