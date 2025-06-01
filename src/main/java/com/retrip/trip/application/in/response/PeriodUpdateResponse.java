package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.entity.Trip;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 기간 수정 Response")
public record PeriodUpdateResponse(
        @Schema(description = "여행 ID")
        UUID tripId,

        @Schema(description = "여행 시작 날짜")
        LocalDate start,

        @Schema(description = "여행 종료 날짜")
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
            @Schema(description = "일정 ID")
            UUID id,

            @Schema(description = "일정 이름")
            String name,

            @Schema(description = "일정 날짜")
            LocalDate date
    ) {}
}
