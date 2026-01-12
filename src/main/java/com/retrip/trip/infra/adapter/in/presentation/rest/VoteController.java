package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.context.UserContext;
import com.retrip.trip.application.in.request.context.WithUserContext;
import com.retrip.trip.application.in.request.vote.VoteCreateRequest;
import com.retrip.trip.application.in.request.vote.VoteUpdateRequest;
import com.retrip.trip.application.in.response.vote.VoteCreateResponse;
import com.retrip.trip.application.in.response.vote.VoteEndResponse;
import com.retrip.trip.application.in.response.vote.VoteUpdateResponse;
import com.retrip.trip.application.in.usecase.VoteManageUseCase;
import com.retrip.trip.domain.exception.annotation.ApiErrorCodeExamples;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.*;

@Tag(name = "Vote", description = "투표 관련 API")
@RequiredArgsConstructor
@RequestMapping("/trips/{tripId}/votes")
@RestController
public class VoteController {
    private final VoteManageUseCase voteManageUseCase;

    @Operation(
            summary = "해당 여행에 투표 생성",
            description = "해당 여행에 투표를 생성하는 API"
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND})
    @PostMapping
    public ApiResponse<VoteCreateResponse> createVote(
            @WithUserContext UserContext userContext,
            @PathVariable UUID tripId,
            @RequestBody VoteCreateRequest request) {
        VoteCreateResponse vote = voteManageUseCase.createVote(userContext.memberId(), tripId, request);
        return ApiResponse.created(vote);
    }

    @Operation(
            summary = "해당 여행에 투표 수정",
            description = "해당 여행에 투표를 수정하는 API"
    )
    @ApiErrorCodeExamples({ENTITY_NOT_FOUND, ILLEGAL_STATE})
    @PutMapping("/{voteId}/update")
    public ApiResponse<VoteUpdateResponse> updateVote(
            @WithUserContext UserContext userContext,
            @PathVariable UUID tripId,
            @PathVariable UUID voteId,
            @RequestBody VoteUpdateRequest request) {
        VoteUpdateResponse vote = voteManageUseCase.updateVote(userContext.memberId(), tripId, voteId, request);
        return ApiResponse.ok(vote);
    }

    @Operation(
            summary = "해당 여행에 투표 종료",
            description = "해당 여행에 투표를 종료하는 API"
    )
    @ApiErrorCodeExamples({ENTITY_NOT_FOUND, VOTE_MODIFY_FORBIDDEN})
    @PutMapping("/{voteId}/end")
    public ApiResponse<VoteEndResponse> endVote(
            @WithUserContext UserContext userContext,
            @PathVariable UUID tripId,
            @PathVariable UUID voteId) {
        VoteEndResponse vote = voteManageUseCase.endVote(userContext.memberId(), tripId, voteId);
        return ApiResponse.ok(vote);
    }

    @Operation(
            summary = "해당 여행에 투표 삭제",
            description = "해당 여행에 투표를 삭제하는 API"
    )
    @ApiErrorCodeExamples({ENTITY_NOT_FOUND, VOTE_MODIFY_FORBIDDEN})
    @DeleteMapping("/{voteId}")
    public ApiResponse<Void> deleteVote(
            @WithUserContext UserContext userContext,
            @PathVariable UUID tripId,
            @PathVariable UUID voteId) {
        voteManageUseCase.deleteVote(userContext.memberId(), tripId, voteId);
        return ApiResponse.noContent();
    }
}

