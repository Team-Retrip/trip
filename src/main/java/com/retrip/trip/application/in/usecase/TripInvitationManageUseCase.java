package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.TripInvitationOrder;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.TripInvitationsCreateResponse;
import com.retrip.trip.application.in.response.TripInvitationsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TripInvitationManageUseCase {
    TripInvitationsCreateResponse createInvitations(UUID tripId, TripInvitationsCreateRequest request);

    Page<TripInvitationsResponse> getTripInvitations(UUID tripId, UUID leaderId, String status, Pageable page, TripInvitationOrder order, String sort);
}
