package com.retrip.trip.application.in.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "여행 일정 세부사항 수정 Request")
public record ItineraryDetailsUpdateRequest(
        @Schema(description = "일정 시간 (선택)")
        LocalDateTime time,

        @Schema(description = "메모 (선택, 최대 200자)")
        String memo,

        @Schema(description = "위치 ID (선택)")
        UUID locationId
) {}