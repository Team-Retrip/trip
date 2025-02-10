package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;

public interface CreateItinerariesUseCase {
    ItinerariesCreateResponse createItineraries(ItinerariesCreateRequest request);
}
