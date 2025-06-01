package com.retrip.trip.application.in.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "여행 일정 생성 Request")
public record ItinerariesCreateRequest(
        @Schema(description = "일정 목록")
        @Size(min = 1)
        List<ItineraryCreateRequest> itineraries
) {
    public List<LocalDate> getDates() {
        return itineraries.stream().map(i -> i.date).toList();
    }

    @Schema(description = "일정 생성 Request")
    public record ItineraryCreateRequest(
            @Schema(description = "일정 날짜", example = "2025-06-15")
            @FutureOrPresent
            LocalDate date
    ) {}
}
