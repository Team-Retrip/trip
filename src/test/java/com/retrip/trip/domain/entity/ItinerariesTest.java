package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripPeriod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ItinerariesTest {
    @DisplayName("여행 기간의 일자 만큼 일정 목록을 생성 한다.")
    @Test
    void ofPeriod() {
        TripPeriod period = new TripPeriod(
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2025, 3, 15));
        Itineraries itineraries = new Itineraries(period);
        assertThat(itineraries.getItineraries().size()).isEqualTo(6);
    }
}
