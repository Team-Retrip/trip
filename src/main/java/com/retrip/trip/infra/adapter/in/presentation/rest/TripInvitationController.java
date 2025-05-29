package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.TripInvitationsCreateResponse;
import com.retrip.trip.application.in.usecase.TripInvitationManageUseCase;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/trips/invitations")
@RestController
public class TripInvitationController {
    private final TripInvitationManageUseCase invitationManageUseCase;

    @PostMapping
    public ApiResponse<TripInvitationsCreateResponse> createInvitation(@RequestBody TripInvitationsCreateRequest request) {
        TripInvitationsCreateResponse invitation = invitationManageUseCase.createInvitations(request);
        return ApiResponse.created(invitation);
    }
}
