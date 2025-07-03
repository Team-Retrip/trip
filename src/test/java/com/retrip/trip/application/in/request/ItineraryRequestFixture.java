package com.retrip.trip.application.in.request;

import java.time.LocalDateTime;
import java.util.UUID;

public class ItineraryRequestFixture {

    public static ItineraryDetailsCreateRequest createItineraryDetails(LocalDateTime time, String description, Long price, UUID locationId) {
        return new ItineraryDetailsCreateRequest(time, description, price, locationId);
    }

    public static ItineraryDetailsUpdateRequest updateItineraryDetails(LocalDateTime time, String description, Long price, UUID locationId) {
        return new ItineraryDetailsUpdateRequest(time, description, price, locationId);
    }
}
