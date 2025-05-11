package com.retrip.trip.application.in.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ItineraryFixture {

    public static ItineraryDetailsCreateRequest createItineraryDetails(LocalDateTime time, String description, Long price, UUID locationId) {
        return new ItineraryDetailsCreateRequest(time, description, price, locationId);
    }
}
