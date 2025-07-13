package com.retrip.trip.application.in.usecase;

import java.util.UUID;

public interface LeaveTripUseCase {
    void leaveTrip(UUID tripId, UUID memberId);
}