package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.ItineraryDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "여행 일정 세부사항 생성 Request")
public record ItineraryDetailsCreateRequest(
        @NotNull
        @Schema(description = "위치 ID")
        UUID locationId
) {
    public ItineraryDetail to(Itinerary itinerary, int sortOrder) {
        return ItineraryDetail.create(null, null, itinerary, locationId, sortOrder);
    }
}