package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.request.vote.VoteCreateRequest;
import com.retrip.trip.application.in.response.vote.VoteCreateResponse;
import com.retrip.trip.application.in.usecase.VoteManageUseCase;
import com.retrip.trip.application.out.repository.VoteRepository;
import com.retrip.trip.domain.entity.vote.Vote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class VoteService implements VoteManageUseCase {
    private final VoteRepository voteRepository;

    @Override
    public VoteCreateResponse createVote(UUID tripId, VoteCreateRequest request) {
        Vote save = voteRepository.save(request.to(tripId));
        return VoteCreateResponse.of(save);
    }
}
