package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;

import java.util.UUID;

public interface UpdateItinerariesUseCase {
    ItinerariesUpdateResponse updateItineraries(UUID tripId, ItinerariesUpdateRequest request);
}
