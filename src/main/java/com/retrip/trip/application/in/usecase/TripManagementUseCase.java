package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.MaxParticipantUpdateRequest;
import com.retrip.trip.application.in.request.PeriodUpdateRequest;

import com.retrip.trip.application.in.response.MaxParticipantUpdateResponse;

import java.util.UUID;

public interface TripManagementUseCase {
    MaxParticipantUpdateResponse updateMaxParticipants(
            UUID tripId, MaxParticipantUpdateRequest request);
}
