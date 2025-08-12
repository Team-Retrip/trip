package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TripRequestFixture {
    public static PeriodUpdateRequest createPeriod(UUID memberId, LocalDate start, LocalDate end) {
        return new PeriodUpdateRequest(memberId, start, end);
    }
}
