package com.retrip.trip.application.in.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여행 참가 신청 거절 Response")
public record TripDemandRejectResponse(
        @Schema(description = "거절 상태", example = "REJECTED", allowableValues = {"REJECTED"})
        String status
) {
    public TripDemandRejectResponse(String status) {
        this.status = status;
    }
}
