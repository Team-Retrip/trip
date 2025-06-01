package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.TripDemandRequest;
import com.retrip.trip.application.in.response.TripDemandResponse;
import java.util.UUID;

public interface TripDemandUseCase {
    TripDemandResponse tripDemand(UUID tripId, TripDemandRequest request);
}
