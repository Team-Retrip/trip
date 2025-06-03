package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.TripInvitationsCreateResponse;

import java.util.UUID;

public interface TripInvitationManageUseCase {
    TripInvitationsCreateResponse createInvitations(UUID tripId, TripInvitationsCreateRequest request);
}
