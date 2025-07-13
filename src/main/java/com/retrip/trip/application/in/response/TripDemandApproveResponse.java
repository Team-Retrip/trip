package com.retrip.trip.application.in.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.retrip.trip.domain.entity.TripDemand;
import java.util.UUID;

@Schema(description = "여행 참가 신청 승인 Response")
public record TripDemandApproveResponse(
        @Schema(description = "승인 회원 ID")
        UUID memberId,
        @Schema(description = "승인 상태 코드")
        String statusCode,
        @Schema(description = "승인 상태 명")
        String statusName
) {

    public static TripDemandApproveResponse of(TripDemand tripDemand) {
        return new TripDemandApproveResponse(tripDemand.getMemberId(), tripDemand.getStatus().getCode(), tripDemand.getStatus().getViewName());
    }
}
