package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.response.TripCreateResponse;

public interface CreateTripUseCase {
    TripCreateResponse createTrip(TripCreateRequest request);
}
