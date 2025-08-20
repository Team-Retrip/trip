package com.retrip.trip.application.in.request.demand;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여행 참가 신청 Request")
public record TripDemandRequest(

        @Schema(description = "신청 메시지")
        String message
) {
}
