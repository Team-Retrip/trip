package com.retrip.trip.application.in.response.demand;

import com.retrip.trip.domain.entity.demand.Demand;
import com.retrip.trip.domain.vo.DemandStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "리더의 여행 참가신청목록 조회 Response")
public record DemandsResponse(
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

    public static DemandsResponse of(Demand demand) {
        return new DemandsResponse(
                demand.getId(),
                demand.getTripId(),
                demand.getMemberId(),
                demand.getMessage(),
                demand.getStatus().name(),
                demand.getStatus().getViewName()
        );
    }
}
