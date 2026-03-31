package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.TripInvitationOrder;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface InvitationManageUseCase {
    InvitationsCreateResponse createInvitations(UUID tripId, UUID leaderId, TripInvitationsCreateRequest request);

    Page<InvitationsResponse> getTripInvitations(UUID tripId, UUID leaderId, Pageable page, TripInvitationOrder order, String sort);

    Page<MemberInvitationResponse> getMemberInvitations(UUID memberId, Pageable page, TripInvitationOrder order, String sort);

    MemberInvitationAcceptResponse acceptMemberInvitations(UUID memberId, UUID tripId, UUID invitationId);

    MemberInvitationRejectResponse rejectMemberInvitations(UUID memberId, UUID tripId, UUID invitationId);

    List<SearchableMemberResponse> searchableMembers(UUID tripId, String name);

    void deleteMemberInvitation(UUID memberId, UUID invitationId);
}
