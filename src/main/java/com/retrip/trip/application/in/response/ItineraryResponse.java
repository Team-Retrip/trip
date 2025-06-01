package com.retrip.trip.application.in.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 일정 Response")
public record ItineraryResponse(
        @Schema(description = "일정 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "일정 날짜", example = "2025-06-15")
        LocalDate date,

        @Schema(description = "일정 이름", example = "파리 시내 관광")
        String name,

        @Schema(description = "일정 세부사항 목록")
        List<ItineraryDetailResponse> itineraryDetailResponse
) {
    @Schema(description = "일정 세부사항 Response")
    public record ItineraryDetailResponse(
            @Schema(description = "세부사항 ID", example = "550e8400-e29b-41d4-a716-446655440001")
            UUID id,

            @Schema(description = "세부사항 설명", example = "에펠탑 관광")
            String description,

            @Schema(description = "일정 시간", example = "2025-06-15T09:00:00")
            LocalDateTime time,

            @Schema(description = "예상 비용", example = "50000")
            Long price,

            @Schema(description = "위치 ID", example = "550e8400-e29b-41d4-a716-446655440002")
            UUID locationId
    ) {}
}
