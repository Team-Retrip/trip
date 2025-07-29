package com.retrip.trip.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.retrip.trip.domain.exception.NotLeaderException;
import com.retrip.trip.domain.exception.PeriodUpdateFailedException;
import com.retrip.trip.domain.fixture.ParticipantFixture;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

class TripTest {
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID destinationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");

    @DisplayName("제목, 설명, 여행지, 기간, 공개 여부, 참가인원수,카테고리를 입력해 여행을 생성할 수 있다.")
    @Test
    void create() {
        assertThatCode(
                        () ->
                                Trip.create(
                                        destinationId,
                                        new TripTitle("속초 여행 멤버 구함"),
                                        new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                                        new TripPeriod(
                                                LocalDate.now().plusDays(1),
                                                LocalDate.now().plusDays(5)),
                                        true,
                                        4,
                                        TripCategory.DOMESTIC))
                .doesNotThrowAnyException();
    }

    @DisplayName("제목, 여행지, 기간, 공개 여부를 입력해 여행과 일정 목록을 생성할 수 있다.")
    @Test
    void createWithItinerary() {
        assertThatCode(
                        () ->
                                Trip.createWithItineraries(
                                        destinationId,
                                        new TripTitle("속초 여행 멤버 구함"),
                                        new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                                        new TripPeriod(
                                                LocalDate.now().plusDays(1),
                                                LocalDate.now().plusDays(5)),
                                        true,
                                        4,
                                        TripCategory.DOMESTIC))
                .doesNotThrowAnyException();
    }

    @Test
    void 여행_일정이_없다면_균등_일정_생성으로_업데이트가_가능하다() {
        TripPeriod period =
                new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(6));
        Trip trip =
                Trip.create(
                        UUID.randomUUID(),
                        new TripTitle("속초 여행 멤버 구함"),
                        new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC);
        // when
        TripPeriod updateTripPeriod =
                new TripPeriod(LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));
        trip.updatePeriod(updateTripPeriod, memberId);

        // then
        assertThat(trip.getPeriod().getStart()).isEqualTo(updateTripPeriod.getStart());
        assertThat(trip.getPeriod().getEnd()).isEqualTo(updateTripPeriod.getEnd());
        assertThat(trip.getItineraries().getValues().size()).isEqualTo(3);
        assertThat(trip.getItineraries().getValues().get(0).getName()).isEqualTo("day 1");
    }
}
