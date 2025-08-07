package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.request.vote.VoteCreateRequest;
import com.retrip.trip.application.in.response.vote.VoteCreateResponse;
import com.retrip.trip.application.in.usecase.VoteManageUseCase;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.application.out.repository.VoteRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.vote.Vote;
import com.retrip.trip.domain.exception.common.EntityNotFoundException;
import com.retrip.trip.domain.service.VotePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class VoteService implements VoteManageUseCase {
    private final VoteRepository voteRepository;
    private final TripRepository tripRepository;
    private final VotePolicy votePolicy;

    @Override
    public VoteCreateResponse createVote(UUID tripId, UUID memberId, VoteCreateRequest request) {
        Trip trip = findTripWithParticipants(tripId);
        votePolicy.canCreate(trip, memberId);
        Vote save = voteRepository.save(request.to(tripId, memberId));
        return VoteCreateResponse.of(save);
    }

    private Trip findTripWithParticipants(UUID tripId) {
        return tripRepository.findWithParticipantsById(tripId)
                .orElseThrow(EntityNotFoundException::new);
    }
}
