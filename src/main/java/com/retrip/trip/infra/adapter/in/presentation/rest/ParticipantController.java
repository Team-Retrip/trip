package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.TripJoinWithPasswordRequest;
import com.retrip.trip.application.in.request.context.UserContext;
import com.retrip.trip.application.in.request.context.WithUserContext;
import com.retrip.trip.application.in.response.TripJoinWithPasswordResponse;
import com.retrip.trip.application.in.usecase.ParticipantManageUseCase;
import com.retrip.trip.domain.exception.annotation.ApiErrorCodeExamples;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.*;

@Tag(name = "Trip-participant", description = "여행 멤버 관련 API")
@RequiredArgsConstructor
@RequestMapping("/trips/{tripId}/participants")
@RestController
public class ParticipantController {
    private final ParticipantManageUseCase participantManageUseCase;

    @Operation(
            summary = "비공개 여행 비밀번호로 참여",
            description = "비공개 여행 비밀번호로 참여하는 API"
    )
    @ApiErrorCodeExamples({TRIP_NOT_FOUND, TRIP_PASSWORD_MISMATCH})
    @PostMapping("/join/password")
    public ApiResponse<TripJoinWithPasswordResponse> joinTripWithPassword(
            @PathVariable UUID tripId,
            @WithUserContext UserContext userContext,
            @RequestBody TripJoinWithPasswordRequest request) {
        TripJoinWithPasswordResponse response = participantManageUseCase.joinTripWithPassword(tripId, request, userContext.memberId());
        return ApiResponse.ok(response);
    }
}
