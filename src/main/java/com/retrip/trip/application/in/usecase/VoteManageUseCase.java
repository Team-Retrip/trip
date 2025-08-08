package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.vote.VoteCreateRequest;
import com.retrip.trip.application.in.request.vote.VoteUpdateRequest;
import com.retrip.trip.application.in.response.vote.VoteCreateResponse;
import com.retrip.trip.application.in.response.vote.VoteUpdateResponse;

import java.util.UUID;

public interface VoteManageUseCase {
    VoteCreateResponse createVote(UUID memberId, UUID tripId, VoteCreateRequest request);

    VoteUpdateResponse updateVote(UUID memberId, UUID tripId, UUID voteId, VoteUpdateRequest request);
}
