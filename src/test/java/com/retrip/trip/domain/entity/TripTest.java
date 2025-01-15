package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripPeriod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;

class TripTest {
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID destinationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");

    @DisplayName("제목, 여행지, 기간, 공개 여부를 입력해 여행을 생성할 수 있다.")
    @Test
    void create() {
        assertThatCode(() -> Trip.createWithItinerary(
                "속초 여행 멤버 구함",
                destinationId,
                new TripPeriod(
                        LocalDate.of(2025, 3, 10),
                        LocalDate.of(2025, 3, 15)),
                true,
                memberId)).doesNotThrowAnyException();
    }
}
