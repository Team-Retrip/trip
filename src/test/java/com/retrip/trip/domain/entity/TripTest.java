package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripPeriod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class TripTest {
    UUID destinationId = UUID.fromString("649d456e-166e-4e8f-a3a7-488a77bca84d");

    @DisplayName("제목, 여행지, 기간, 공개 여부를 입력해 여행을 생성할 수 있다.")
    @Test
    void create() {
        assertThatCode(() -> Trip.createWithItinerary(
                "속초 여행 멤버 구함",
                destinationId,
                new TripPeriod(
                        LocalDate.of(2025, 3, 10),
                        LocalDate.of(2025, 3, 15)),
                true)).doesNotThrowAnyException();
    }

    @DisplayName("여행 리더를 등록할 수 있다.")
    @Test
    void leader() {
        Trip trip = Trip.createWithItinerary(
                "속초 여행 멤버 구함",
                destinationId,
                new TripPeriod(
                        LocalDate.of(2025, 3, 10),
                        LocalDate.of(2025, 3, 15)),
                true);
        UUID leaderId = UUID.randomUUID();
        trip.registerLeader(leaderId);
        assertThat(trip.getLeaderId()).isEqualTo(leaderId);
    }
}
