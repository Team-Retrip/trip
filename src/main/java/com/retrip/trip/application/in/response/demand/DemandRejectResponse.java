package com.retrip.trip.application.in.response.demand;

import com.retrip.trip.domain.vo.DemandStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "여행 참가 신청 거절 Response")
public record DemandRejectResponse(
        @Schema(description = "거절 회원 ID")
        UUID memberId,
        @Schema(description = "거절 상태 코드")
        String statusCode,
        @Schema(description = "거절 상태 명")
        String statusName
) {

    public static DemandRejectResponse of(UUID memberId, DemandStatus status) {
        return new DemandRejectResponse(memberId, status.name(), status.getViewName());
    }
}