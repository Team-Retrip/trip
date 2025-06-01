package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
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
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(6));
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
        assertThat(itineraries.getValues().size()).isEqualTo(6);
    }

    @DisplayName("일정의 날짜가 기간을 벗어나면 예외가 발생한다.")
    @Test
    void out_of_period() {
        TripPeriod period = new TripPeriod(
                LocalDate.now().plusDays(0),
                LocalDate.now().plusDays(5)
        );
        List<LocalDate> dates = List.of(
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(0));

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
        assertThatThrownBy(() -> new Itineraries(trip, period, dates))
                .isExactlyInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("날짜 목록으로 일정을 생성한다.")
    @Test
    void irregular() {
        TripPeriod period = new TripPeriod(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(6));
        List<LocalDate> dates = List.of(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(6));

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
        Itineraries itineraries = new Itineraries(trip, period, dates);
        assertThat(itineraries.getValues().size()).isEqualTo(3);
        assertThat(itineraries.getValues().get(0).getName()).isEqualTo("day 1");
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 시작 전 ~ 이전 일정 시작 전'으로 변경할 수 있다")
    void updatePeriodIsBeforePrePeriodStart() {
        //given
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(15));
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

        //when
        TripPeriod updatePeriod = new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        itineraries.updateByPeriod(updatePeriod, trip);


        //then
        assertThat(itineraries.getValues()).hasSize(3);
        assertThat(itineraries.getValues().stream().map(Itinerary::getName).toList())
                .containsExactly("day 1", "day 2", "day 3");
        assertThat(itineraries.getValues().stream().map(Itinerary::getDate).toList())
                .containsExactly(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2),
                        LocalDate.now().plusDays(3)
                );
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 시작 전 ~ 이전 일정 종료 전'으로 변경할 수 있다")
    void updatePeriodBetweenPrePeriodStartBeforeAndPrePeriodEndBefore() {
        //given
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(15));
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

        //when
        TripPeriod updatePeriod = new TripPeriod(LocalDate.now().plusDays(3), LocalDate.now().plusDays(8));
        itineraries.updateByPeriod(updatePeriod, trip);
        

        //then
        assertThat(itineraries.getValues()).hasSize(6);
        assertThat(itineraries.getValues().stream().map(Itinerary::getName).toList())
                .containsExactly("day 1", "day 2", "day 3", "day 4", "day 5", "day 6");
        assertThat(itineraries.getValues().stream().map(Itinerary::getDate).toList())
                .containsExactly(
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(4),
                        LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(6),
                        LocalDate.now().plusDays(7),
                        LocalDate.now().plusDays(8)
                );
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 시작 전 ~ 이전 일정 종료 후'으로 변경할 수 있다")
    void updatePeriodBetweenPrePeriodStartBeforeAndPrePeriodEndAfter() {
        //given
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));
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

        //when
        TripPeriod updatePeriod = new TripPeriod(LocalDate.now().plusDays(3), LocalDate.now().plusDays(8));
        itineraries.updateByPeriod(updatePeriod, trip);

        //then
        assertThat(itineraries.getValues()).hasSize(6);
        assertThat(itineraries.getValues().stream().map(Itinerary::getName).toList())
                .containsExactly("day 1", "day 2", "day 3", "day 4", "day 5", "day 6");
        assertThat(itineraries.getValues().stream().map(Itinerary::getDate).toList())
                .containsExactly(
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(4),
                        LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(6),
                        LocalDate.now().plusDays(7),
                        LocalDate.now().plusDays(8)
                );
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 시작 후 ~ 이전 일정 종료 전'으로 변경할 수 있다")
    void updatePeriodBetweenPrePeriodStartAfterAndPrePeriodEndBefore() {
        //given
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
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

        //when
        TripPeriod updatePeriod = new TripPeriod(LocalDate.now().plusDays(7), LocalDate.now().plusDays(9));
        itineraries.updateByPeriod(updatePeriod, trip);

        //then
        assertThat(itineraries.getValues()).hasSize(3);
        assertThat(itineraries.getValues().stream().map(Itinerary::getName).toList())
                .containsExactly("day 1", "day 2", "day 3");
        assertThat(itineraries.getValues().stream().map(Itinerary::getDate).toList())
                .containsExactly(
                        LocalDate.now().plusDays(7),
                        LocalDate.now().plusDays(8),
                        LocalDate.now().plusDays(9)
                );
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 시작 후 ~ 이전 일정 종료 후'으로 변경할 수 있다")
    void updatePeriodBetweenPrePeriodStartAfterAndPrePeriodEndAfter() {
        //given
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
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

        //when
        TripPeriod updatePeriod = new TripPeriod(LocalDate.now().plusDays(7), LocalDate.now().plusDays(12));
        itineraries.updateByPeriod(updatePeriod, trip);

        //then
        assertThat(itineraries.getValues()).hasSize(6);
        assertThat(itineraries.getValues().stream().map(Itinerary::getName).toList())
                .containsExactly("day 1", "day 2", "day 3", "day 4", "day 5", "day 6");
        assertThat(itineraries.getValues().stream().map(Itinerary::getDate).toList())
                .containsExactly(
                        LocalDate.now().plusDays(7),
                        LocalDate.now().plusDays(8),
                        LocalDate.now().plusDays(9),
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(11),
                        LocalDate.now().plusDays(12)
                );
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 종료 후'으로 변경할 수 있다")
    void updatePeriodIsAfterPrePeriodEndAfter() {
        //given
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
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

        //when
        TripPeriod updatePeriod = new TripPeriod(LocalDate.now().plusDays(11), LocalDate.now().plusDays(14));
        itineraries.updateByPeriod(updatePeriod, trip);

        //then
        assertThat(itineraries.getValues()).hasSize(4);
        assertThat(itineraries.getValues().stream().map(Itinerary::getName).toList())
                .containsExactly("day 1", "day 2", "day 3", "day 4");
        assertThat(itineraries.getValues().stream().map(Itinerary::getDate).toList())
                .containsExactly(
                        LocalDate.now().plusDays(11),
                        LocalDate.now().plusDays(12),
                        LocalDate.now().plusDays(13),
                        LocalDate.now().plusDays(14)
                );
    }

    @Test
    @DisplayName("여행 상세 일정을 제거할 수 있다.")
    void deleteItineraryDetail() {
        //given
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
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

        //when
        trip.getItineraries().getValues().getFirst().removeItineraryDetail(UUID.randomUUID());


        //then

    }
}
