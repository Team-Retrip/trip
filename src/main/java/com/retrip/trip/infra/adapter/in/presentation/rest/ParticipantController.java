package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.TripJoinWithPasswordRequest;
import com.retrip.trip.application.in.response.TripJoinWithPasswordResponse;
import com.retrip.trip.application.in.usecase.ParticipantManageUseCase;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/trips/{tripId}/participants")
@RestController
@Tag(name = "Trip", description = "여행 멤버 관련 API")
public class ParticipantController {
    private final ParticipantManageUseCase participantManageUseCase;

    @PostMapping("/join/password")
    @Schema(description = "비공개 여행 비밀번호로 참여")
    public ApiResponse<TripJoinWithPasswordResponse> joinTripWithPassword(
            @PathVariable UUID tripId,
            @RequestBody TripJoinWithPasswordRequest request) {
        TripJoinWithPasswordResponse response = participantManageUseCase.joinTripWithPassword(tripId, request);
        return ApiResponse.ok(response);
    }
}
