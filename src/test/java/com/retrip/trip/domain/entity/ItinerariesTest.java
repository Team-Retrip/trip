package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripPeriod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItinerariesTest {
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");

    @DisplayName("여행 기간의 일자 만큼 일정 목록을 생성 한다.")
    @Test
    void ofPeriod() {
        Trip trip = Trip.createWithItinerary(
                "속초 여행 멤버 구함",
                locationId,
                new TripPeriod(
                        LocalDate.of(2025, 3, 10),
                        LocalDate.of(2025, 3, 15)),
                true,
                memberId);
        TripPeriod period = new TripPeriod(
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2025, 3, 15));
        Itineraries itineraries = new Itineraries(trip, period);
        assertThat(itineraries.getItineraries().size()).isEqualTo(6);
    }
}
