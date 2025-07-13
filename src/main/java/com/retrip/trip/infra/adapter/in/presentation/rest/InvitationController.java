package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.response.MemberInvitationsResponse;
import com.retrip.trip.application.in.request.TripInvitationOrder;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.InvitationsCreateResponse;
import com.retrip.trip.application.in.response.InvitationsResponse;
import com.retrip.trip.application.in.usecase.InvitationManageUseCase;
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
public class InvitationController {
    private final InvitationManageUseCase invitationManageUseCase;

    @PostMapping("/{tripId}/invitations")
    public ApiResponse<InvitationsCreateResponse> createInvitations(
            @PathVariable UUID tripId,
            @RequestBody TripInvitationsCreateRequest request) {
        InvitationsCreateResponse invitation = invitationManageUseCase.createInvitations(tripId, request);
        return ApiResponse.created(invitation);
    }

    @GetMapping("/{tripId}/invitations")
    public ApiResponse<Page<InvitationsResponse>> getTripInvitations(
            @PathVariable UUID tripId,
            @RequestParam UUID leaderId,
            @RequestParam String status,
            @PageableDefault(size = 10, page = 0) Pageable page,
            @RequestParam(name = "order", defaultValue = "DATE") TripInvitationOrder order,
            @RequestParam(name = "sort", defaultValue = "desc") String sort) {
        Page<InvitationsResponse> invitations =
                invitationManageUseCase.getTripInvitations(tripId, leaderId, status, page, order, sort);
        return ApiResponse.ok(invitations);
    }

    @GetMapping("/members/{memberId}/invitations")
    public ApiResponse<Page<MemberInvitationsResponse>> getMemberInvitations(
            @PathVariable UUID memberId,
            @RequestParam String status,
            @PageableDefault(size = 10, page = 0) Pageable page,
            @RequestParam(name = "order", defaultValue = "DATE") TripInvitationOrder order,
            @RequestParam(name = "sort", defaultValue = "desc") String sort) {
        Page<MemberInvitationsResponse> invitations =
                invitationManageUseCase.getMemberInvitations(memberId, status, page, order, sort);
        return ApiResponse.ok(invitations);
    }
}
