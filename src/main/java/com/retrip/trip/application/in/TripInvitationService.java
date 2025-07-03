package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.TripInvitationOrder;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.MemberTripInvitationsResponse;
import com.retrip.trip.application.in.response.TripInvitationsCreateResponse;
import com.retrip.trip.application.in.response.TripInvitationsResponse;
import com.retrip.trip.application.in.usecase.TripInvitationManageUseCase;
import com.retrip.trip.application.out.repository.TripInvitationReadRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripInvitation;
import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.TripNotFoundException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.EntityNotFoundException;
import com.retrip.trip.domain.vo.TripInvitationStatus;
import com.retrip.trip.infra.adapter.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class TripInvitationService implements TripInvitationManageUseCase {
    private final TripRepository tripRepository;
    private final TripInvitationReadRepository tripInvitationReadRepository;

    @Override
    public TripInvitationsCreateResponse createInvitations(UUID tripId, TripInvitationsCreateRequest request) {
        Trip trip = findTripWithInvitations(tripId);
        trip.createInvitations(request.leaderId(), request.memberIds());
        return TripInvitationsCreateResponse.of(trip);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TripInvitationsResponse> getTripInvitations(
            UUID tripId, UUID leaderId, String status, Pageable page, TripInvitationOrder order, String sort) {
        validateLeader(findTrip(tripId), leaderId, new MemberIsNotLeaderException());
        Pageable pageable = PaginationUtils.createPageRequest(page, order.getField(), sort);
        Page<TripInvitation> tripInvitations =
                tripInvitationReadRepository.findByTripIdAndStatus(tripId, TripInvitationStatus.valueOf(status), pageable);
        return tripInvitations.map(TripInvitationsResponse::of);
    }

    @Override
    public Page<MemberTripInvitationsResponse> getMemberTripInvitations(
            UUID memberId, String status, Pageable page, TripInvitationOrder order, String sort) {
        Pageable pageable = PaginationUtils.createPageRequest(page, order.getField(), sort);
        Page<TripInvitation> tripInvitations =
                tripInvitationReadRepository.findByMemberIdAndStatus(memberId, TripInvitationStatus.valueOf(status), pageable);
        return tripInvitations.map(MemberTripInvitationsResponse::of);
    }

    private Trip findTripWithInvitations(UUID tripId) {
        return tripRepository.findWithTripInvitations(tripId)
                .orElseThrow(TripNotFoundException::new);
    }

    private Trip findTrip(UUID tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(EntityNotFoundException::new);
    }

    private void validateLeader(Trip trip, UUID leaderId, BusinessException exception) {
        if (!trip.getTripParticipants().isLeader(leaderId)) {
            throw exception;
        }
    }
}
