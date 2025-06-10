package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.response.MemberTripInvitationsResponse;
import com.retrip.trip.application.in.request.TripInvitationOrder;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.TripInvitationsCreateResponse;
import com.retrip.trip.application.in.response.TripInvitationsResponse;
import com.retrip.trip.application.in.usecase.TripInvitationManageUseCase;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
public class TripInvitationController {
    private final TripInvitationManageUseCase invitationManageUseCase;

    @PostMapping("/{tripId}/invitations")
    public ApiResponse<TripInvitationsCreateResponse> createInvitation(
            @PathVariable UUID tripId,
            @RequestBody TripInvitationsCreateRequest request) {
        TripInvitationsCreateResponse invitation = invitationManageUseCase.createInvitations(tripId, request);
        return ApiResponse.created(invitation);
    }

    @GetMapping("/{tripId}/invitations")
    public ApiResponse<Page<TripInvitationsResponse>> getTripInvitations(
            @PathVariable UUID tripId,
            @RequestParam UUID leaderId,
            @RequestParam String status,
            @PageableDefault(size = 10, page = 0) Pageable page,
            @RequestParam(name = "order", defaultValue = "DATE") TripInvitationOrder order,
            @RequestParam(name = "sort", defaultValue = "desc") String sort) {
        Page<TripInvitationsResponse> invitations =
                invitationManageUseCase.getTripInvitations(tripId, leaderId, status, page, order, sort);
        return ApiResponse.ok(invitations);
    }

    @GetMapping("/members/{memberId}/invitations")
    public ApiResponse<Page<MemberTripInvitationsResponse>> getMemberTripInvitations(
            @PathVariable UUID memberId,
            @RequestParam String status,
            @PageableDefault(size = 10, page = 0) Pageable page,
            @RequestParam(name = "order", defaultValue = "DATE") TripInvitationOrder order,
            @RequestParam(name = "sort", defaultValue = "desc") String sort) {
        Page<MemberTripInvitationsResponse> invitations =
                invitationManageUseCase.getMemberTripInvitations(memberId, status, page, order, sort);
        return ApiResponse.ok(invitations);
    }
}
