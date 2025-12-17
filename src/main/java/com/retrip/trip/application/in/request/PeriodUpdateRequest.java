package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.vo.TripPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "여행 기간 수정 Request")
public record PeriodUpdateRequest(
        @Schema(description = "여행 시작 날짜")
        @FutureOrPresent
        LocalDate start,

        @Schema(description = "여행 종료 날짜")
        @FutureOrPresent
        LocalDate end
) {
    public TripPeriod toPeriod() {
        return new TripPeriod(start, end);
    }
}
