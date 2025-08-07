package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripUpdateVisibilityRequest;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.response.TripUpdateVisibilityResponse;

import java.util.UUID;

public interface TripManageUseCase {
    TripCreateResponse createTrip(TripCreateRequest request);

    TripCreateResponse createTripWithItineraries(TripCreateRequest request);

    TripUpdateVisibilityResponse updateTripVisibility(UUID tripId, TripUpdateVisibilityRequest request);
}
