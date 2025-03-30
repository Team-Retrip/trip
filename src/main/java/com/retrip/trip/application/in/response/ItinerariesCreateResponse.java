package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Itineraries;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 일정 생성 Response")
public record ItinerariesCreateResponse(

        @Schema(description = "여행 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID tripId,

        @Schema(description = "생성된 일정 리스트")
        List<ItineraryCreateResponse> itineraries
) {
    public static ItinerariesCreateResponse of(UUID tripId, Itineraries itineraries) {
        return new ItinerariesCreateResponse(
                tripId,
                itineraries.getValues().stream()
                        .map(i -> new ItineraryCreateResponse(i.getId(), i.getName(), i.getDate()))
                        .toList()
        );
    }

    @Schema(description = "단일 일정 Response")
    private record ItineraryCreateResponse(
            @Schema(description = "일정 ID", example = "550e8400-e29b-41d4-a716-446655440001")
            UUID id,

            @Schema(description = "일정 이름", example = "런던 투어")
            String name,

            @Schema(description = "일정 날짜", example = "2025-06-15")
            LocalDate date
    ) {
    }
}
