package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripPeriod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItinerariesTest {
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");

    @DisplayName("여행 기간의 일자 만큼 일정 목록을 생성 한다.")
    @Test
    void ofPeriod() {
        TripPeriod period = new TripPeriod(
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2025, 3, 15));
        Trip trip = Trip.createWithItineraries(
                "속초 여행 멤버 구함",
                locationId,
                period,
                true,
                memberId);
        Itineraries itineraries = new Itineraries(trip, period);
        assertThat(itineraries.getValues().size()).isEqualTo(6);
    }

    @DisplayName("일정의 날짜가 기간을 벗어나면 예외가 발생한다.")
    @Test
    void out_of_period() {
        TripPeriod period = new TripPeriod(
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2025, 3, 15));
        List<LocalDate> dates = List.of(
                LocalDate.of(2025, 3, 9),
                LocalDate.of(2025, 3, 10));

        Trip trip = Trip.createWithItineraries(
                "속초 여행 멤버 구함",
                locationId,
                period,
                true,
                memberId);
        assertThatThrownBy(() -> new Itineraries(trip, period, dates))
                .isExactlyInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("날짜 목록으로 일정을 생성한다.")
    @Test
    void irregular() {
        TripPeriod period = new TripPeriod(
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2025, 3, 15));
        List<LocalDate> dates = List.of(
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2025, 3, 12),
                LocalDate.of(2025, 3, 15));

        Trip trip = Trip.createWithItineraries(
                "속초 여행 멤버 구함",
                locationId,
                period,
                true,
                memberId);
        Itineraries itineraries = new Itineraries(trip, period, dates);
        assertThat(itineraries.getValues().size()).isEqualTo(3);
        assertThat(itineraries.getValues().get(0).getName()).isEqualTo("day 1");
    }
}
