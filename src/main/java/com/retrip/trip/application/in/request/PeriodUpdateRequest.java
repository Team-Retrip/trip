package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.vo.TripPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "여행 기간 수정 Request")
public record PeriodUpdateRequest(
        @Schema(description = "멤버 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull
        UUID memberId,

        @Schema(description = "여행 시작 날짜", example = "2025-06-15")
        @FutureOrPresent
        LocalDate start,

        @Schema(description = "여행 종료 날짜", example = "2025-06-25")
        @FutureOrPresent
        LocalDate end
) {
    public TripPeriod toPeriod() {
        return new TripPeriod(start, end);
    }
}
