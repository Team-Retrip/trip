package com.retrip.trip.application.in.response.demand;

import com.retrip.trip.domain.vo.DemandStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "여행 참가 신청 Response")
public record DemandResponse(
        @Schema(description = "여행 신청 ID")
        UUID demandId,

        @Schema(description = "여행 ID")
        UUID tripId,

        @Schema(description = "신청자 멤버 ID")
        UUID memberId,

        @Schema(description = "신청 메시지")
        String message,

        @Schema(description = "신청 상태")
        String statusCode,

        @Schema(description = "신청 상태 명")
        String statusName
) {

    public static DemandResponse of(UUID demandId, UUID tripId, UUID memberId, String message, DemandStatus status) {
        return new DemandResponse(
                demandId,
                tripId,
                memberId,
                message,
                status.name(),
                status.getViewName()
        );
    }
}
