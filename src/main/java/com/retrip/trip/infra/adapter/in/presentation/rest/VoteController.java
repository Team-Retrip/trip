package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.context.UserContext;
import com.retrip.trip.application.in.request.context.WithUserContext;
import com.retrip.trip.application.in.request.vote.VoteCreateRequest;
import com.retrip.trip.application.in.request.vote.VoteUpdateRequest;
import com.retrip.trip.application.in.response.vote.VoteCreateResponse;
import com.retrip.trip.application.in.response.vote.VoteEndResponse;
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
            @WithUserContext UserContext userContext,
            @PathVariable UUID tripId,
            @RequestBody VoteCreateRequest request) {
        VoteCreateResponse vote = voteManageUseCase.createVote(userContext.memberId(), tripId, request);
        return ApiResponse.created(vote);
    }

    @PutMapping("/{voteId}/update")
    public ApiResponse<VoteUpdateResponse> updateVote(
            @WithUserContext UserContext userContext,
            @PathVariable UUID tripId,
            @PathVariable UUID voteId,
            @RequestBody VoteUpdateRequest request) {
        VoteUpdateResponse vote = voteManageUseCase.updateVote(userContext.memberId(), tripId, voteId, request);
        return ApiResponse.ok(vote);
    }

    @PutMapping("/{voteId}/end")
    public ApiResponse<VoteEndResponse> endVote(
            @WithUserContext UserContext userContext,
            @PathVariable UUID tripId,
            @PathVariable UUID voteId) {
        VoteEndResponse vote = voteManageUseCase.endVote(userContext.memberId(), tripId, voteId);
        return ApiResponse.ok(vote);
    }

    @DeleteMapping("/{voteId}")
    public ApiResponse<Void> deleteVote(
            @WithUserContext UserContext userContext,
            @PathVariable UUID tripId,
            @PathVariable UUID voteId) {
        voteManageUseCase.deleteVote(userContext.memberId(), tripId, voteId);
        return ApiResponse.noContent();
    }
}

