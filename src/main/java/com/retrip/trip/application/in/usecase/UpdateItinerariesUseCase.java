package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;


public interface UpdateItinerariesUseCase {
    ItinerariesUpdateResponse updateItineraries(ItinerariesUpdateRequest request);
}
