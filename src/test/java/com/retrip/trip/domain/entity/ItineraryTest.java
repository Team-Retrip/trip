package com.retrip.trip.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItineraryTest {
    @DisplayName("여행 일자로 일정을 생성하면 'day + 일자' 이름을 갖는다.")
    @Test
    void create() {
        assertThat(Itinerary.create(new Trip(), 1, LocalDate.now()).getName()).isEqualTo("day 1");
    }

    @DisplayName("여행 일자는 1 이상 이어야 한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void create_less_than_1(int day) {
        assertThatThrownBy(() -> Itinerary.create(new Trip(), day, LocalDate.now()))
                .isExactlyInstanceOf(RuntimeException.class);
    }
}
