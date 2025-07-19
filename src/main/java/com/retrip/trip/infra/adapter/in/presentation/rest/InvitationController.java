package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.TripInvitationOrder;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.*;
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
    public ApiResponse<Page<MemberInvitationResponse>> getMemberInvitations(
            @PathVariable UUID memberId,
            @RequestParam String status,
            @PageableDefault(size = 10, page = 0) Pageable page,
            @RequestParam(name = "order", defaultValue = "DATE") TripInvitationOrder order,
            @RequestParam(name = "sort", defaultValue = "desc") String sort) {
        Page<MemberInvitationResponse> invitations =
                invitationManageUseCase.getMemberInvitations(memberId, status, page, order, sort);
        return ApiResponse.ok(invitations);
    }

    @PutMapping("/members/{memberId}/trips/{tripId}/invitations/{invitationId}/accept")
    public ApiResponse<MemberInvitationAcceptResponse> acceptMemberInvitations(
            @PathVariable UUID memberId,
            @PathVariable UUID tripId,
            @PathVariable UUID invitationId) {
        MemberInvitationAcceptResponse invitation =
                invitationManageUseCase.acceptMemberInvitations(memberId, tripId, invitationId);
        return ApiResponse.ok(invitation);
    }

    @PutMapping("/members/{memberId}/trips/{tripId}/invitations/{invitationId}/reject")
    public ApiResponse<MemberInvitationRejectResponse> rejectMemberInvitations(
            @PathVariable UUID memberId,
            @PathVariable UUID tripId,
            @PathVariable UUID invitationId) {
        MemberInvitationRejectResponse invitation =
                invitationManageUseCase.rejectMemberInvitations(memberId, tripId, invitationId);
        return ApiResponse.ok(invitation);
    }
}
