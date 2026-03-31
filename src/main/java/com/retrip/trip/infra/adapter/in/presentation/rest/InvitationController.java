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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.*;

@RequiredArgsConstructor
@RequestMapping("/trips")
@RestController
@Tag(name = "Invitation", description = "여행 초대 관련 API")
public class InvitationController {
    private final InvitationManageUseCase invitationManageUseCase;

    @Operation(
            summary = "초대 가능한 사용자 검색",
            description = "이름으로 사용자를 검색하고 이미 여행에 참여 중인 멤버는 제외합니다."
    )
    @GetMapping("/{tripId}/invitations/searchable-members")
    public ApiResponse<List<SearchableMemberResponse>> searchableMembers(
            @Parameter(description = "여행 ID", required = true) @PathVariable UUID tripId,
            @Parameter(description = "검색할 회원 이름 (부분 일치)", required = true, example = "박정수") @RequestParam String name) {
        return ApiResponse.ok(invitationManageUseCase.searchableMembers(tripId, name));
    }

    @Operation(
            summary = "여행 초대",
            description = "리더가 회원 ID 목록을 지정해 초대장을 일괄 발송합니다."
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, MEMBER_IS_NOT_LEADER, TRIP_INVITATION_DUPLICATE, ILLEGAL_STATE})
    @PostMapping("/{tripId}/invitations")
    public ApiResponse<InvitationsCreateResponse> createInvitations(
            @WithUserContext UserContext userContext,
            @Parameter(description = "여행 ID", required = true) @PathVariable UUID tripId,
            @RequestBody TripInvitationsCreateRequest request) {
        InvitationsCreateResponse invitation = invitationManageUseCase.createInvitations(tripId, userContext.memberId(), request);
        return ApiResponse.created(invitation);
    }

    @Operation(
            summary = "보낸 초대장 목록 조회 (리더 전용)",
            description = "리더가 해당 여행에 보낸 모든 초대장 목록을 조회합니다. 상태(대기/수락/거절/만료) 구분 없이 전체 반환됩니다."
    )
    @ApiErrorCodeExample(MEMBER_IS_NOT_LEADER)
    @GetMapping("/{tripId}/invitations")
    public ApiResponse<Page<InvitationsResponse>> getTripInvitations(
            @WithUserContext UserContext userContext,
            @Parameter(description = "여행 ID", required = true) @PathVariable UUID tripId,
            @PageableDefault(size = 10, page = 0) Pageable page,
            @Parameter(description = "정렬 기준 (DATE: 초대 일시)", example = "DATE") @RequestParam(name = "order", defaultValue = "DATE") TripInvitationOrder order,
            @Parameter(description = "정렬 방향 (asc / desc)", example = "desc") @RequestParam(name = "sort", defaultValue = "desc") String sort) {
        Page<InvitationsResponse> invitations =
                invitationManageUseCase.getTripInvitations(tripId, userContext.memberId(), page, order, sort);
        return ApiResponse.ok(invitations);
    }

    @Operation(
            summary = "받은 초대장 목록 조회 (본인)",
            description = "로그인한 회원이 받은 모든 초대장 목록을 조회합니다. 상태(대기/수락/거절/만료) 구분 없이 전체 반환됩니다."
    )
    @GetMapping("/members/invitations")
    public ApiResponse<Page<MemberInvitationResponse>> getMemberInvitations(
            @WithUserContext UserContext userContext,
            @PageableDefault(size = 10, page = 0) Pageable page,
            @Parameter(description = "정렬 기준 (DATE: 초대 일시)", example = "DATE") @RequestParam(name = "order", defaultValue = "DATE") TripInvitationOrder order,
            @Parameter(description = "정렬 방향 (asc / desc)", example = "desc") @RequestParam(name = "sort", defaultValue = "desc") String sort) {
        Page<MemberInvitationResponse> invitations =
                invitationManageUseCase.getMemberInvitations(userContext.memberId(), page, order, sort);
        return ApiResponse.ok(invitations);
    }

    @Operation(
            summary = "받은 초대장 삭제",
            description = "거절되거나 만료된 초대장을 목록에서 삭제합니다. 대기 중이거나 수락된 초대장은 삭제할 수 없습니다."
    )
    @ApiErrorCodeExamples({INVITATION_NOT_FOUND, INVITATION_CANNOT_DELETE, HANDLE_ACCESS_DENIED})
    @DeleteMapping("/members/invitations/{invitationId}")
    public ApiResponse<Void> deleteMemberInvitation(
            @WithUserContext UserContext userContext,
            @Parameter(description = "초대장 ID", required = true) @PathVariable UUID invitationId) {
        invitationManageUseCase.deleteMemberInvitation(userContext.memberId(), invitationId);
        return ApiResponse.noContent();
    }

    @Operation(
            summary = "받은 초대 수락",
            description = "받은 여행 초대를 수락합니다. 수락 시 여행 참가자로 자동 등록됩니다."
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, INVITATION_NOT_FOUND, INVITATION_EXPIRED, TRIP_PARTICIPANTS_IS_FULL, TRIP_FULL})
    @PutMapping("/members/trips/{tripId}/invitations/{invitationId}/accept")
    public ApiResponse<MemberInvitationAcceptResponse> acceptMemberInvitations(
            @WithUserContext UserContext userContext,
            @Parameter(description = "여행 ID", required = true) @PathVariable UUID tripId,
            @Parameter(description = "초대장 ID", required = true) @PathVariable UUID invitationId) {
        MemberInvitationAcceptResponse invitation =
                invitationManageUseCase.acceptMemberInvitations(userContext.memberId(), tripId, invitationId);
        return ApiResponse.ok(invitation);
    }

    @Operation(
            summary = "받은 초대 거절",
            description = "받은 여행 초대를 거절합니다."
    )
    @ApiErrorCodeExamples({INVITATION_NOT_FOUND, INVITATION_REJECT_NOT_ALLOWED})
    @PutMapping("/members/trips/{tripId}/invitations/{invitationId}/reject")
    public ApiResponse<MemberInvitationRejectResponse> rejectMemberInvitations(
            @WithUserContext UserContext userContext,
            @Parameter(description = "여행 ID", required = true) @PathVariable UUID tripId,
            @Parameter(description = "초대장 ID", required = true) @PathVariable UUID invitationId) {
        MemberInvitationRejectResponse invitation =
                invitationManageUseCase.rejectMemberInvitations(userContext.memberId(), tripId, invitationId);
        return ApiResponse.ok(invitation);
    }
}
