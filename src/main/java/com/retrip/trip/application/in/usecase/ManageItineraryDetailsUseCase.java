package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.ItineraryDetailsCreateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsUpdateRequest;

import com.retrip.trip.application.in.response.ItineraryDetailsCreateResponse;

import com.retrip.trip.application.in.response.ItineraryDetailsUpdateResponse;

import java.util.UUID;

public interface ManageItineraryDetailsUseCase {
    ItineraryDetailsCreateResponse createItineraryDetails(UUID tripId, UUID itineraryId, ItineraryDetailsCreateRequest request);

    ItineraryDetailsUpdateResponse updateItineraryDetails(UUID tripId, UUID itineraryId, UUID itineraryDetailId, ItineraryDetailsUpdateRequest request);

    void deleteItineraryDetail(UUID tripId, UUID itineraryId, UUID itineraryDetailsId);

}
