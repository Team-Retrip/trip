package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.vote.VoteCreateRequest;
import com.retrip.trip.application.in.response.vote.VoteCreateResponse;
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
            @PathVariable UUID tripId,
            @RequestBody VoteCreateRequest request) {
        VoteCreateResponse vote = voteManageUseCase.createVote(tripId, request);
        return ApiResponse.created(vote);
    }
}

