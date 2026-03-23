package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.ItineraryDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "세부일정 일괄 생성 Request (실행취소 복구용)")
public record ItineraryDetailsBulkCreateRequest(
        @NotNull
        @Schema(description = "복구할 세부일정 목록 (sortOrder 오름차순 정렬 권장)")
        List<Item> items
) {
    public record Item(
            @NotNull
            @Schema(description = "위치 ID")
            UUID locationId,

            @Schema(description = "메모")
            String memo,

            @Schema(description = "일정 시간")
            LocalDateTime time
    ) {
        public ItineraryDetail to(Itinerary itinerary) {
            return ItineraryDetail.create(memo, time, itinerary, locationId, 0); // sortOrder는 도메인이 재할당
        }
    }
}
