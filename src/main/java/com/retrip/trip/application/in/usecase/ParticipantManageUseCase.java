package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.TripJoinWithPasswordRequest;
import com.retrip.trip.application.in.response.TripJoinWithPasswordResponse;

import java.util.UUID;

public interface ParticipantManageUseCase {
    TripJoinWithPasswordResponse joinTripWithPassword(UUID tripId, TripJoinWithPasswordRequest request);
}
