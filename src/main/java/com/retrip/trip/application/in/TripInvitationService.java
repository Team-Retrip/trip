package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.TripInvitationsCreateResponse;
import com.retrip.trip.application.in.usecase.TripInvitationManageUseCase;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.exception.TripNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class TripInvitationService implements TripInvitationManageUseCase {
    private final TripRepository tripRepository;

    @Override
    public TripInvitationsCreateResponse createInvitations(TripInvitationsCreateRequest request) {
        Trip trip = findTripWithInvitations(request.tripId());
        trip.createInvitations(request.leaderId(), request.memberIds());
        return TripInvitationsCreateResponse.of(trip);
    }

    private Trip findTripWithInvitations(UUID tripId) {
        return tripRepository.findWithTripInvitations(tripId)
                .orElseThrow(TripNotFoundException::new);
    }
}
