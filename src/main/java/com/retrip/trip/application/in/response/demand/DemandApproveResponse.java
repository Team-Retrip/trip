package com.retrip.trip.application.in.response.demand;

import com.retrip.trip.domain.vo.DemandStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "여행 참가 신청 승인 Response")
public record DemandApproveResponse(
        @Schema(description = "승인 회원 ID")
        UUID memberId,
        @Schema(description = "승인 상태 코드")
        String statusCode,
        @Schema(description = "승인 상태 명")
        String statusName
) {

    public static DemandApproveResponse of(UUID memberId, DemandStatus status) {
        return new DemandApproveResponse(memberId, status.name(), status.getViewName());
    }
}
