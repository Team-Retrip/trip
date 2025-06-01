package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.entity.Trip;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 기간 수정 Response")
public record PeriodUpdateResponse(
        @Schema(description = "여행 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID tripId,

        @Schema(description = "여행 시작 날짜", example = "2025-06-15")
        LocalDate start,

        @Schema(description = "여행 종료 날짜", example = "2025-06-25")
        LocalDate end,

        @Schema(description = "수정된 일정 목록")
        List<ItineraryUpdateResponse> itineraries
) {
    private static List<ItineraryUpdateResponse> toList(Itineraries itineraries) {
        return itineraries.getValues().stream()
                .map(i -> new ItineraryUpdateResponse(i.getId(), i.getName(), i.getDate()))
                .toList();
    }

    public static PeriodUpdateResponse of(Trip trip) {
        return new PeriodUpdateResponse(
                trip.getId(), trip.getPeriod().getStart(), trip.getPeriod().getEnd(),
                toList(trip.getItineraries())
        );
    }

    @Schema(description = "일정 수정 Response")
    public record ItineraryUpdateResponse(
            @Schema(description = "일정 ID", example = "550e8400-e29b-41d4-a716-446655440001")
            UUID id,

            @Schema(description = "일정 이름", example = "파리 시내 관광")
            String name,

            @Schema(description = "일정 날짜", example = "2025-06-16")
            LocalDate date
    ) {}
}
