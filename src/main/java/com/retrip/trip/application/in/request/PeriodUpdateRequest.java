package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.vo.TripPeriod;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PeriodUpdateRequest(
        @NotNull UUID updateId, @FutureOrPresent LocalDate start, @FutureOrPresent LocalDate end) {

    public TripPeriod toPeriod() {
        return new TripPeriod(start, end);
    }
}
