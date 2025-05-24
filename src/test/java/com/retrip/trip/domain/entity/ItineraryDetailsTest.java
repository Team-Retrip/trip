package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItineraryDetailsTest {
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");

    @DisplayName("여행 상세 일정은 여행 일정과 일정이 같아야 한다.")
    @Test
    void ItineraryDetailsDayIsEqualToItineraryDay() {
        TripPeriod period = new TripPeriod(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5));
        Trip trip = Trip.createWithItineraries(
                memberId,
                UUID.randomUUID(),
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                period,
                true,
                4,
                TripCategory.DOMESTIC
        );

        Itineraries itineraries = new Itineraries(trip, period);
        Itinerary itinerary = itineraries.getValues().getFirst();
        ItineraryDetail itineraryDetail
                = ItineraryDetail.create(2000L, "속초 여행", LocalDateTime.now(), itinerary, locationId);

        assertThatThrownBy(() -> itinerary.addItineraryDetail(itineraryDetail))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("같은 시간에 상세 일정이 있으면 안된다.")
    @Test
    void canNotItineraryDetailsTimeConflicting() {
        TripPeriod period = new TripPeriod(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5));
        Trip trip = Trip.createWithItineraries(
                memberId,
                UUID.randomUUID(),
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                period,
                true,
                4,
                TripCategory.DOMESTIC
        );

        Itineraries itineraries = new Itineraries(trip, period);
        Itinerary itinerary = itineraries.getValues().getFirst();
        ItineraryDetail itineraryDetail
                = ItineraryDetail.create(2000L, "속초 여행", LocalDateTime.now().plusDays(1), itinerary, locationId);
        itinerary.addItineraryDetail(itineraryDetail);

        ItineraryDetail itineraryDetail2
                = ItineraryDetail.create(2000L, "속초 여행", LocalDateTime.now().plusDays(1), itinerary, locationId);

        assertThatThrownBy(() -> itinerary.addItineraryDetail(itineraryDetail2))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
