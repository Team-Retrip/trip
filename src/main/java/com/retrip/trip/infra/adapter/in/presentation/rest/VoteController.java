package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.vote.VoteCreateRequest;
import com.retrip.trip.application.in.request.vote.VoteUpdateRequest;
import com.retrip.trip.application.in.response.vote.VoteCreateResponse;
import com.retrip.trip.application.in.response.vote.VoteUpdateResponse;
import com.retrip.trip.application.in.usecase.VoteManageUseCase;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/trips/{tripId}/votes")
@RestController
public class VoteController {
    private final VoteManageUseCase voteManageUseCase;

    @PostMapping
    public ApiResponse<VoteCreateResponse> createVote(
            @RequestParam UUID memberId,
            @PathVariable UUID tripId,
            @RequestBody VoteCreateRequest request) {
        VoteCreateResponse vote = voteManageUseCase.createVote(memberId, tripId, request);
        return ApiResponse.created(vote);
    }

    @PutMapping("/{voteId}")
    public ApiResponse<VoteUpdateResponse> updateVote(
            @RequestParam UUID memberId,
            @PathVariable UUID tripId,
            @PathVariable UUID voteId,
            @RequestBody VoteUpdateRequest request) {
        VoteUpdateResponse vote = voteManageUseCase.updateVote(memberId, tripId, voteId, request);
        return ApiResponse.ok(vote);
    }
}

