package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripDemand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "여행 참가 신청 Request")
public record TripDemandRequest(
        @Schema(description = "신청자 멤버 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull
        UUID memberId,

        @Schema(description = "신청 메시지", example = "함께 여행하고 싶습니다!")
        String message
) {
    public TripDemand to(Trip trip) {
        return TripDemand.create(memberId, trip, message);
    }
}
