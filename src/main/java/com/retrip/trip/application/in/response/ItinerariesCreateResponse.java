package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Itineraries;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 일정 목록 생성 Response")
public record ItinerariesCreateResponse(
        @Schema(description = "여행 ID")
        UUID tripId,

        @Schema(description = "생성된 일정 목록")
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

    @Schema(description = "생성된 일정 정보")
    private record ItineraryCreateResponse(
            @Schema(description = "일정 ID")
            UUID id,

            @Schema(description = "일정 이름")
            String name,

            @Schema(description = "일정 날짜")
            LocalDate date
    ) {
    }
}