package com.retrip.trip.application.in.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "여행 최대 참여자 수정 Response")
public record MaxParticipantUpdateResponse(
        @Schema(description = "여행 Id") UUID tripId,
        @Schema(description = "여행 최대 참여자") int maxParticipants) {

    public static MaxParticipantUpdateResponse of(UUID id, int maxParticipants) {
        return new MaxParticipantUpdateResponse(id, maxParticipants);
    }
}
