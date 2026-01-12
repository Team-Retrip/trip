package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.request.vote.VoteCreateRequest;
import com.retrip.trip.application.in.request.vote.VoteUpdateRequest;
import com.retrip.trip.application.in.response.vote.VoteCreateResponse;
import com.retrip.trip.application.in.response.vote.VoteEndResponse;
import com.retrip.trip.application.in.response.vote.VoteUpdateResponse;
import com.retrip.trip.application.in.usecase.VoteManageUseCase;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.application.out.repository.VoteRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.vote.Vote;
import com.retrip.trip.domain.entity.vote.VoteOptions;
import com.retrip.trip.domain.exception.TripNotFoundException;
import com.retrip.trip.domain.exception.common.EntityNotFoundException;
import com.retrip.trip.domain.service.VotePolicy;
import com.retrip.trip.domain.vo.vote.VotePeriod;
import com.retrip.trip.domain.vo.vote.VoteSetting;
import com.retrip.trip.domain.vo.vote.VoteSummary;
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
    public VoteCreateResponse createVote(UUID memberId, UUID tripId, VoteCreateRequest request) {
        Trip trip = findTrip(tripId);
        votePolicy.canCreate(trip, memberId);
        Vote save = voteRepository.save(request.to(tripId, memberId));
        return VoteCreateResponse.of(save);
    }

    @Override
    public VoteUpdateResponse updateVote(UUID memberId, UUID tripId, UUID voteId, VoteUpdateRequest request) {
        Vote vote = findVote(voteId);

        VoteSummary voteSummary = request.toSummary();
        VoteSetting voteSetting = request.toSetting();
        VotePeriod votePeriod = request.toPeriod();
        VoteOptions voteOptions = request.toOptions();
        vote.update(voteSummary, voteSetting, votePeriod, voteOptions, memberId);

        return VoteUpdateResponse.of(vote);
    }

    @Override
    public VoteEndResponse endVote(UUID memberId, UUID tripId, UUID voteId) {
        Vote vote = findVote(voteId);
        vote.end(memberId);
        return VoteEndResponse.of(vote);
    }

    @Override
    public void deleteVote(UUID memberId, UUID tripId, UUID voteId) {
        Vote vote = findVote(voteId);
        vote.validateDeletable(memberId);
        voteRepository.delete(vote);
    }

    private Vote findVote(UUID voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(EntityNotFoundException::new);
    }

    private Trip findTrip(UUID tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(TripNotFoundException::new);
    }
}
