package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.ItineraryDetail;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "여행 일정 세부사항 생성 Request")
public record ItineraryDetailsCreateRequest(
        @Schema(description = "일정 시간", example = "2025-06-15T09:00:00")
        LocalDateTime time,

        @Schema(description = "일정 설명", example = "에펠탑 관광")
        String description,

        @Schema(description = "예상 비용", example = "50000")
        Long price,

        @Schema(description = "위치 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID locationId
) {
    public ItineraryDetail to(Itinerary itinerary) {
        return ItineraryDetail.create(price, description, time, itinerary, locationId);
    }
}
