package com.retrip.trip.domain.fixture;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.UUID;

public class TripFixture {
    public static final UUID TRIP_ID = UUID.fromString("b56d1d03-894a-4fe5-afbe-19be496bc1b4");
    public static final UUID LEADER_ID = UUID.fromString("caec62d1-f29d-477d-9743-292f48cc66bb");
    public static final UUID MEMBER_ID = UUID.fromString("85e05380-3693-4f3f-b74b-203715d15df8");
    public static final UUID 정수_ID = UUID.fromString("a7f7215b-081a-42f4-b3e2-f06393de2f8b");
    public static final UUID 홍석_ID = UUID.fromString("bf97d20b-d1f7-46a9-8362-11b9fa02d67d");
    public static final UUID 준호_ID = UUID.fromString("8b9b67fd-1d88-4b30-bfea-cd8f89fc10d9");
    public static final UUID 지수_ID = UUID.fromString("de3b60d2-5672-464d-8769-bf5c9de5eaff");
    public static final UUID 혁진_ID = UUID.fromString("42880aaf-4b97-4b0c-8a8a-72df4bb592f6");

    public static Trip createTrip(UUID tripId) {
        Trip trip = Trip.createWithItineraries(
                LEADER_ID,
                UUID.randomUUID(),
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)),
                true,
                4,
                TripCategory.DOMESTIC);
        ReflectionTestUtils.setField(trip, "id", tripId);
        return trip;
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
