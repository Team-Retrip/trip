package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.TripInvitationsCreateResponse;
import com.retrip.trip.application.in.usecase.TripInvitationManageUseCase;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/trips/{tripId}/invitations")
@RestController
public class TripInvitationController {
    private final TripInvitationManageUseCase invitationManageUseCase;

    @PostMapping
    public ApiResponse<TripInvitationsCreateResponse> createInvitation(
            @PathVariable UUID tripId,
            @RequestBody TripInvitationsCreateRequest request) {
        TripInvitationsCreateResponse invitation = invitationManageUseCase.createInvitations(tripId, request);
        return ApiResponse.created(invitation);
    }
}
