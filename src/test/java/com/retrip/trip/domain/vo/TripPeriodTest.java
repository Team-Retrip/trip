package com.retrip.trip.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class TripPeriodTest {
    @DisplayName("시작 일자와 종료 일자로 여행 기간을 생성 한다.")
    @Test
    void create() {
        assertThatCode(() -> new TripPeriod(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(6)))
                .doesNotThrowAnyException();
    }

    @DisplayName("여행 시작 일자는 현재 날짜 보다 작을 수 없다.")
    @Test
    void start_date_less_than_current_date() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        assertThatThrownBy(() -> new TripPeriod(
                yesterday,
                LocalDate.now().plusDays(1)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("여행 종료 일자가 시작 일자 보다 작을 수 없다.")
    @Test
    void end_date_less_than_start_date() {
        assertThatThrownBy(() -> new TripPeriod(
                LocalDate.now().plusDays(1),
                LocalDate.now().minusDays(1)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("여행 기간은 30일을 초과할 수 없다.")
    @Test
    void period_within_limit_30_days() {
        assertThatThrownBy(() -> new TripPeriod(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusMonths(2)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("여행 기간으로 총 여행일을 가져온다.")
    @Test
    void getDays() {

        TripPeriod period = new TripPeriod(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(6));

        //TripPeriod period = new TripPeriod(LocalDate.of(2025, 5, 31),LocalDate.of(2025, 7, 2));
        assertThat(period.getDays()).isEqualTo(6);


    }
}
