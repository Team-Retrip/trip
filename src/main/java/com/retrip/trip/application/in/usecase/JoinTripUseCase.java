package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.TripJoinRequest;
import com.retrip.trip.application.in.response.TripJoinResponse;

public interface JoinTripUseCase {
    TripJoinResponse joinTrip(TripJoinRequest request);
}
