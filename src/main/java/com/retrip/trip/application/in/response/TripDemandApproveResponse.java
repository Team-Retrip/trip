package com.retrip.trip.application.in.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여행 참가 신청 승인 Response")
public record TripDemandApproveResponse(
        @Schema(description = "승인 상태", example = "APPROVED", allowableValues = {"APPROVED"})
        String status
) {
    public TripDemandApproveResponse(String status) {
        this.status = status;
    }
}
