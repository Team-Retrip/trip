package com.retrip.trip.application.in.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ItineraryRequestFixture {

    public static ItineraryDetailsCreateRequest createItineraryDetails(UUID locationId) {
        return new ItineraryDetailsCreateRequest(locationId);
    }

    public static ItineraryDetailsUpdateRequest updateItineraryDetails(LocalDateTime time, String memo, UUID locationId) {
        return new ItineraryDetailsUpdateRequest(time, memo, locationId);
    }

    public static ItineraryDetailsBulkCreateRequest bulkCreateItineraryDetails(List<ItineraryDetailsBulkCreateRequest.Item> items) {
        return new ItineraryDetailsBulkCreateRequest(items);
    }

    public static ItineraryDetailsBulkCreateRequest.Item bulkItem(UUID locationId, String memo, LocalDateTime time) {
        return new ItineraryDetailsBulkCreateRequest.Item(locationId, memo, time);
    }
}