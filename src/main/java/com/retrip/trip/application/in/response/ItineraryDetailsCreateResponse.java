package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.ItineraryDetail;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "여행 일정 세부사항 생성 Response")
public record ItineraryDetailsCreateResponse(
        @Schema(description = "일정 세부사항 ID")
        UUID id,

        @Schema(description = "일정 시간")
        LocalDateTime time,

        @Schema(description = "일정 설명")
        String description,

        @Schema(description = "예상 비용")
        Long price,

        @Schema(description = "위치 ID")
        UUID locationId
) {
    public static ItineraryDetailsCreateResponse of(ItineraryDetail itineraryDetail) {
        return new ItineraryDetailsCreateResponse(
                itineraryDetail.getId(),
                itineraryDetail.getTime().getValue(),
                itineraryDetail.getDescription().getValue(),
                itineraryDetail.getPrice().getValue(),
                itineraryDetail.getLocationId()
        );
    }
}
