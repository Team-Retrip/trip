package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripDemand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "여행 참가 신청 Request")
public record TripDemandRequest(
        @Schema(description = "신청자 멤버 ID")
        @NotNull
        UUID memberId,

        @Schema(description = "신청 메시지")
        String message
) {
    public TripDemand to(Trip trip) {
        return TripDemand.create(memberId, trip, message);
    }
}
