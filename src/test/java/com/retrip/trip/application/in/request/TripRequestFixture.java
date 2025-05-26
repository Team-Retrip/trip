package com.retrip.trip.application.in.request;

import java.time.LocalDate;
import java.util.UUID;

public class TripRequestFixture {
    public static PeriodUpdateRequest createPeriod(UUID memberId, LocalDate start, LocalDate end) {
        return new PeriodUpdateRequest(memberId, start, end);
    }
}
