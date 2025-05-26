package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
import java.time.LocalDate;
import java.util.UUID;

public class TripRequestFixture {
    public static PeriodUpdateRequest createPeriod(UUID memberId, LocalDate start, LocalDate end) {
        return new PeriodUpdateRequest(memberId, start, end);
    }

    public static Trip createTestTrip(UUID memberId, String title, String description, TripCategory category) {
        TripPeriod period = createFuturePeriod();
        return Trip.create(
                memberId,
                UUID.randomUUID(),
                new TripTitle(title),
                new TripDescription(description),
                period,
                true,
                4,
                category);
    }

    private static TripPeriod createFuturePeriod() {
        return new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
    }
}
