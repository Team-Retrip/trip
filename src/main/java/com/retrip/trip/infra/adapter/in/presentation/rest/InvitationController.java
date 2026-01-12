package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.TripInvitationOrder;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.request.context.UserContext;
import com.retrip.trip.application.in.request.context.WithUserContext;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.usecase.InvitationManageUseCase;
import com.retrip.trip.domain.exception.annotation.ApiErrorCodeExample;
import com.retrip.trip.domain.exception.annotation.ApiErrorCodeExamples;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.*;
import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_PARTICIPANTS_IS_FULL;

@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
@Tag(name = "Invitation", description = "여행 초대 관련 API")
public class InvitationController {
    private final InvitationManageUseCase invitationManageUseCase;

    @Operation(
            summary = "여행 초대",
            description = "여행 초대장을 생성해 해당 회원들에세 초대하는 API"
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, MEMBER_IS_NOT_LEADER, TRIP_INVITATION_DUPLICATE, ILLEGAL_STATE})
    @PostMapping("/{tripId}/invitations")
    public ApiResponse<InvitationsCreateResponse> createInvitations(
            @PathVariable UUID tripId,
            @RequestBody TripInvitationsCreateRequest request) {
        InvitationsCreateResponse invitation = invitationManageUseCase.createInvitations(tripId, request);
        return ApiResponse.created(invitation);
    }

    @Operation(
            summary = "해당 여행에 초대한 초대장 목록 조회",
            description = "해당 여행에 초대한 초대장 목록 조회하는 API"
    )
    @ApiErrorCodeExample(MEMBER_IS_NOT_LEADER)
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

    @Operation(
            summary = "내가 받은 초대장 목록 조회",
            description = "내가 받은 초대장 목록 조회하는 API"
    )
    @GetMapping("/members/invitations")
    public ApiResponse<Page<MemberInvitationResponse>> getMemberInvitations(
            @WithUserContext UserContext userContext,
            @RequestParam String status,
            @PageableDefault(size = 10, page = 0) Pageable page,
            @RequestParam(name = "order", defaultValue = "DATE") TripInvitationOrder order,
            @RequestParam(name = "sort", defaultValue = "desc") String sort) {
        Page<MemberInvitationResponse> invitations =
                invitationManageUseCase.getMemberInvitations(userContext.memberId(), status, page, order, sort);
        return ApiResponse.ok(invitations);
    }

    @Operation(
            summary = "내가 받은 여행 초대 수락",
            description = "내가 받은 여행 초대 수락하는 API"
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, INVITATION_NOT_FOUND, INVITATION_EXPIRED, TRIP_PARTICIPANTS_IS_FULL, TRIP_FULL})
    @PutMapping("/members/trips/{tripId}/invitations/{invitationId}/accept")
    public ApiResponse<MemberInvitationAcceptResponse> acceptMemberInvitations(
            @WithUserContext UserContext userContext,
            @PathVariable UUID tripId,
            @PathVariable UUID invitationId) {
        MemberInvitationAcceptResponse invitation =
                invitationManageUseCase.acceptMemberInvitations(userContext.memberId(), tripId, invitationId);
        return ApiResponse.ok(invitation);
    }

    @Operation(
            summary = "내가 받은 여행 초대 거절",
            description = "내가 받은 여행 초대 거절하는 API"
    )
    @ApiErrorCodeExamples({INVITATION_NOT_FOUND, INVITATION_REJECT_NOT_ALLOWED})
    @PutMapping("/members/trips/{tripId}/invitations/{invitationId}/reject")
    public ApiResponse<MemberInvitationRejectResponse> rejectMemberInvitations(
            @WithUserContext UserContext userContext,
            @PathVariable UUID tripId,
            @PathVariable UUID invitationId) {
        MemberInvitationRejectResponse invitation =
                invitationManageUseCase.rejectMemberInvitations(userContext.memberId(), tripId, invitationId);
        return ApiResponse.ok(invitation);
    }
}
