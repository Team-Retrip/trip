package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.request.TripInvitationOrder;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.usecase.InvitationManageUseCase;
import com.retrip.trip.application.out.gateway.MemberGateway;
import com.retrip.trip.application.out.repository.InvitationRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.entity.invitation.Invitations;
import com.retrip.trip.domain.exception.TripNotFoundException;
import com.retrip.trip.domain.exception.common.EntityNotFoundException;
import com.retrip.trip.domain.service.InvitationPolicy;
import com.retrip.trip.domain.vo.InvitationStatus;
import com.retrip.trip.infra.adapter.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.retrip.trip.domain.exception.common.ErrorCode.INVITATION_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class InvitationService implements InvitationManageUseCase {
    private final TripRepository tripRepository;
    private final InvitationRepository invitationRepository;
    private final InvitationPolicy invitationPolicy;
    private final MemberGateway memberGateway;

    @Override
    public InvitationsCreateResponse createInvitations(UUID tripId, UUID leaderId, TripInvitationsCreateRequest request) {
        Trip trip = findTrip(tripId);
        invitationPolicy.canInvite(trip, leaderId, request.memberIds());
        Invitations invitations = new Invitations(invitationRepository.findByTripId(tripId));
        invitations.add(tripId, request.memberIds());
        List<Invitation> savedInvitations = invitationRepository.saveAll(invitations.getValues());
        return InvitationsCreateResponse.of(tripId, savedInvitations);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<InvitationsResponse> getTripInvitations(
            UUID tripId, UUID leaderId, Pageable page, TripInvitationOrder order, String sort) {
        invitationPolicy.canViewInvitations(findTrip(tripId), leaderId);
        Pageable pageable = PaginationUtils.createPageRequest(page, order.getField(), sort);
        Page<Invitation> tripInvitations = invitationRepository.findByTripId(tripId, pageable);

        List<UUID> memberIds = tripInvitations.getContent().stream()
                .map(Invitation::getMemberId).distinct().toList();
        Map<UUID, MemberGateway.MemberInfo> memberInfoMap = memberGateway.getMembersByIds(memberIds).stream()
                .collect(Collectors.toMap(MemberGateway.MemberInfo::id, Function.identity()));

        return tripInvitations.map(inv -> InvitationsResponse.of(inv, memberInfoMap.get(inv.getMemberId())));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MemberInvitationResponse> getMemberInvitations(
            UUID memberId, Pageable page, TripInvitationOrder order, String sort) {
        Pageable pageable = PaginationUtils.createPageRequest(page, order.getField(), sort);
        Page<Invitation> tripInvitations = invitationRepository.findByMemberId(memberId, pageable);

        List<UUID> tripIds = tripInvitations.getContent().stream()
                .map(Invitation::getTripId)
                .distinct()
                .collect(Collectors.toList());
        Map<UUID, Trip> tripMap = tripRepository.findAllById(tripIds).stream()
                .collect(Collectors.toMap(Trip::getId, t -> t));

        return tripInvitations.map(inv -> MemberInvitationResponse.of(inv, tripMap.get(inv.getTripId())));
    }

    @Override
    public MemberInvitationAcceptResponse acceptMemberInvitations(UUID memberId, UUID tripId, UUID invitationId) {
        Trip trip = findTrip(tripId);
        Invitation invitation = findInvitation(invitationId);
        invitationPolicy.canAccept(trip, invitation);
        invitation.accept();
        trip.addParticipant(TripParticipant.createTripParticipant(memberId, trip));
        return MemberInvitationAcceptResponse.of(invitation);
    }

    @Override
    public MemberInvitationRejectResponse rejectMemberInvitations(UUID memberId, UUID tripId, UUID invitationId) {
        Invitation invitation = findInvitation(invitationId);
        invitationPolicy.canReject(invitation);
        invitation.reject();
        return MemberInvitationRejectResponse.of(invitation);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SearchableMemberResponse> searchableMembers(UUID tripId, String name) {
        Trip trip = findTrip(tripId);
        Set<UUID> participantIds = trip.getTripParticipants().getValues().stream()
                .map(TripParticipant::getMemberId)
                .collect(Collectors.toSet());

        return memberGateway.searchMembersByName(name).stream()
                .filter(m -> !participantIds.contains(m.id()))
                .map(SearchableMemberResponse::of)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMemberInvitation(UUID memberId, UUID invitationId) {
        Invitation invitation = findInvitation(invitationId);
        invitationPolicy.canDelete(invitation, memberId);
        invitationRepository.deleteById(invitationId);
    }

    private Trip findTrip(UUID tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(TripNotFoundException::new);
    }

    private Invitation findInvitation(UUID invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException(INVITATION_NOT_FOUND));
    }
}
