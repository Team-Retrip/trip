package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsCreateRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;

import com.retrip.trip.application.in.response.ItineraryDetailsCreateResponse;

import java.util.UUID;

public interface ManageItineraryDetailsUseCase {
    ItineraryDetailsCreateResponse createItineraryDetails(UUID tripId, UUID itineraryId, ItineraryDetailsCreateRequest request);

    void deleteItineraryDetail(UUID tripId, UUID itineraryId, UUID itineraryDetailsId);
}
