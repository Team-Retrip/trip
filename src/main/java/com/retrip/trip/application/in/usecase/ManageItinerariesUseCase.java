package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;

import com.retrip.trip.application.in.response.ItineraryDetailsUpdateResponse;

import java.util.UUID;

public interface ManageItinerariesUseCase {
    ItinerariesUpdateResponse updateItineraries(UUID tripId, ItinerariesUpdateRequest request);

    ItineraryDetailsUpdateResponse updateItineraryDetails(
            UUID tripId, UUID itineraryId, ItineraryDetailsUpdateRequest request);
}
