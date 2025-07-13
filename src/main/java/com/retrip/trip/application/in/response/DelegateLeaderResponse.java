package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Trip;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "여행 리더 위임 Response")
public record DelegateLeaderResponse(
        @Schema(description = "여행 ID")
        UUID tripId,

        @Schema(description = "새로운 리더 멤버 ID")
        UUID newLeaderId
) {
    public static DelegateLeaderResponse of(Trip trip, UUID newLeaderId) {
        return new DelegateLeaderResponse(trip.getId(), newLeaderId);
    }
}