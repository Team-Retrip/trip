package com.retrip.trip.application.in.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 일정 Response")
public record ItineraryResponse(
        @Schema(description = "일정 ID")
        UUID id,

        @Schema(description = "일정 날짜")
        LocalDate date,

        @Schema(description = "일정 이름")
        String name,

        @Schema(description = "일정 세부사항 목록")
        List<ItineraryDetailResponse> itineraryDetailResponse
) {
    @Schema(description = "일정 세부사항 Response")
    public record ItineraryDetailResponse(
            @Schema(description = "세부사항 ID")
            UUID id,

            @Schema(description = "세부사항 설명")
            String description,

            @Schema(description = "일정 시간")
            LocalDateTime time,

            @Schema(description = "예상 비용")
            Long price,

            @Schema(description = "위치 ID")
            UUID locationId
    ) {}
}
