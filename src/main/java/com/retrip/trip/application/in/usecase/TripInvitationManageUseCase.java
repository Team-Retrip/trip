package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.TripInvitationsCreateResponse;

public interface TripInvitationManageUseCase {
    TripInvitationsCreateResponse createInvitations(TripInvitationsCreateRequest request);
}
