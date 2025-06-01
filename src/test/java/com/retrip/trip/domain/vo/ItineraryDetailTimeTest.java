package com.retrip.trip.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ItineraryDetailTimeTest {
    @Test
    @DisplayName("시간만 가지고 온다.")
    void getTime() {
        //given
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 11, 53, 14, 22);

        ItineraryDetailTime response = new ItineraryDetailTime(time);

        //when & then
        assertThat(response.getValue().getYear()).isEqualTo(time.getYear());
        assertThat(response.getValue().getMonthValue()).isEqualTo(time.getMonthValue());
        assertThat(response.getValue().getDayOfMonth()).isEqualTo(time.getDayOfMonth());
        assertThat(response.getValue().getHour()).isEqualTo(time.getHour());
        assertThat(response.getValue().getMinute()).isEqualTo(0);
        assertThat(response.getValue().getSecond()).isEqualTo(0);
    }

    @Test
    @DisplayName("시간 비교")
    void equalsTest() {
        //given
        LocalDateTime time1 = LocalDateTime.of(2020, 1, 1, 11, 53, 14, 22);
        LocalDateTime time2 = LocalDateTime.of(2020, 1, 1, 11, 0, 12, 22);

        ItineraryDetailTime response = new ItineraryDetailTime(time1);
        boolean result = response.equals(new ItineraryDetailTime(time2));

        //when & then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("시간 비교")
    void notEqualsTest() {
        //given
        LocalDateTime time1 = LocalDateTime.of(2020, 1, 1, 21, 0, 12, 22);
        LocalDateTime time2 = LocalDateTime.of(2020, 1, 1, 11, 0, 12, 22);

        ItineraryDetailTime response = new ItineraryDetailTime(time1);
        boolean result = response.equals(new ItineraryDetailTime(time2));

        //when & then
        assertThat(result).isFalse();
    }
}
