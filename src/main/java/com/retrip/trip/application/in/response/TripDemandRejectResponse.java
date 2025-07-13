package com.retrip.trip.application.in.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.retrip.trip.domain.entity.TripDemand;
import java.util.UUID;

@Schema(description = "여행 참가 신청 거절 Response")
public record TripDemandRejectResponse(
        @Schema(description = "거절 회원 ID")
        UUID memberId,
        @Schema(description = "거절 상태 코드")
        String statusCode,
        @Schema(description = "거절 상태 명")
        String statusName
) {

    public static TripDemandRejectResponse of(TripDemand tripDemand) {
        return new TripDemandRejectResponse(tripDemand.getMemberId(), tripDemand.getStatus().getCode(), tripDemand.getStatus().getViewName());
    }
}