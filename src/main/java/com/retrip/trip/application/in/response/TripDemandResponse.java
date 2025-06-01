package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.TripDemand;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "여행 참가 신청 Response")
public record TripDemandResponse(
        @Schema(description = "여행 신청 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID tripDemandId,

        @Schema(description = "여행 ID", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID tripId,

        @Schema(description = "신청자 멤버 ID", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID memberId,

        @Schema(description = "신청 상태", example = "PENDING")
        String status
) {
    public static TripDemandResponse of(TripDemand tripDemand) {
        return new TripDemandResponse(
                tripDemand.getId(),
                tripDemand.getTrip().getId(),
                tripDemand.getMemberId(),
                tripDemand.getStatus().getViewName()
        );
    }
}
