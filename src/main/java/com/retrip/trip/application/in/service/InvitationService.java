package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.request.TripInvitationOrder;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.InvitationsCreateResponse;
import com.retrip.trip.application.in.response.InvitationsResponse;
import com.retrip.trip.application.in.response.MemberInvitationsResponse;
import com.retrip.trip.application.in.usecase.InvitationManageUseCase;
import com.retrip.trip.application.out.repository.InvitationRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.entity.invitation.Invitations;
import com.retrip.trip.domain.exception.common.EntityNotFoundException;
import com.retrip.trip.domain.service.InvitationPolicy;
import com.retrip.trip.domain.service.TripPolicy;
import com.retrip.trip.domain.vo.InvitationStatus;
import com.retrip.trip.infra.adapter.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class InvitationService implements InvitationManageUseCase {
    private final TripRepository tripRepository;
    private final InvitationRepository invitationRepository;
    private final TripPolicy tripPolicy;
    private final InvitationPolicy invitationPolicy;

    @Override
    public InvitationsCreateResponse createInvitations(UUID tripId, TripInvitationsCreateRequest request) {
        Trip trip = findTripWithParticipants(tripId);
        invitationPolicy.canInvite(trip, request.leaderId(), request.memberIds());
        Invitations invitations = new Invitations(invitationRepository.findByTripId(tripId));
        invitations.add(tripId, request.memberIds());
        List<Invitation> savedInvitations = invitationRepository.saveAll(invitations.getValues());
        return InvitationsCreateResponse.of(tripId, savedInvitations);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<InvitationsResponse> getTripInvitations(
            UUID tripId, UUID leaderId, String status, Pageable page, TripInvitationOrder order, String sort) {
        tripPolicy.validateLeader(findTripWithParticipants(tripId), leaderId);
        Pageable pageable = PaginationUtils.createPageRequest(page, order.getField(), sort);
        Page<Invitation> tripInvitations =
                invitationRepository.findByTripIdAndStatus(tripId, InvitationStatus.valueOf(status), pageable);
        return tripInvitations.map(InvitationsResponse::of);
    }

    @Override
    public Page<MemberInvitationsResponse> getMemberInvitations(
            UUID memberId, String status, Pageable page, TripInvitationOrder order, String sort) {
        Pageable pageable = PaginationUtils.createPageRequest(page, order.getField(), sort);
        Page<Invitation> tripInvitations =
                invitationRepository.findByMemberIdAndStatus(memberId, InvitationStatus.valueOf(status), pageable);
        return tripInvitations.map(MemberInvitationsResponse::of);
    }

    private Trip findTripWithParticipants(UUID tripId) {
        return tripRepository.findWithParticipantsById(tripId)
                .orElseThrow(EntityNotFoundException::new);
    }
}
