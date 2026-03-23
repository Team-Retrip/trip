package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.ItineraryDetail;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "여행 일정 세부사항 수정 Response")
public record ItineraryDetailsUpdateResponse(
        @Schema(description = "일정 세부사항 ID")
        UUID id,

        @Schema(description = "위치 ID")
        UUID locationId,

        @Schema(description = "일정 시간")
        LocalDateTime time,

        @Schema(description = "메모")
        String memo,

        @Schema(description = "정렬 순서")
        int sortOrder,

        @Schema(description = "장소명 (TODO: map service API 연동 후 locationId로 조회)")
        String locationName
) {
    public static ItineraryDetailsUpdateResponse of(ItineraryDetail detail) {
        // TODO: map service API 호출하여 locationId → locationName 조회
        return new ItineraryDetailsUpdateResponse(
                detail.getId(),
                detail.getLocationId(),
                detail.getTimeValue(),
                detail.getMemoValue(),
                detail.getSortOrder(),
                null
        );
    }
}