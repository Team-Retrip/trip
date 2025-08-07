package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.vote.VoteCreateRequest;
import com.retrip.trip.application.in.response.vote.VoteCreateResponse;

import java.util.UUID;

public interface VoteManageUseCase {
    VoteCreateResponse createVote(UUID tripId, VoteCreateRequest request);
}
