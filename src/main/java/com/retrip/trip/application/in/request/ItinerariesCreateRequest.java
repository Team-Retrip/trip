package com.retrip.trip.application.in.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "일정 생성 Request")
public record ItinerariesCreateRequest(

        @Schema(description = "여행 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull
        UUID tripId,

        @Schema(description = "일정 리스트", example = "[{\"date\": \"2025-06-15\"}, {\"date\": \"2025-06-16\"}]")
        @Size(min = 1, message = "최소 한 개 이상의 일정이 필요합니다.")
        List<ItineraryCreateRequest> itineraries
) {
    public List<LocalDate> getDates() {
        return itineraries.stream().map(i -> i.date).toList();
    }

    @Schema(description = "단일 일정 Request")
    public record ItineraryCreateRequest(
            @Schema(description = "일정 날짜", example = "2025-06-15")
            @FutureOrPresent(message = "현재 날짜 또는 미래 날짜만 입력 가능합니다.")
            LocalDate date
    ) {
    }
}
