package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;

import java.util.UUID;

public interface ManageItinerariesUseCase {
  ItinerariesCreateResponse createItineraries(UUID tripId, ItinerariesCreateRequest request);
}
