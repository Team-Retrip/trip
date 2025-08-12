package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.PeriodUpdateFailedException;
import com.retrip.trip.domain.vo.ParticipantRole;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TripTest {
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID destinationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");

    @DisplayName("제목, 설명, 여행지, 기간, 공개 여부, 참가인원수,카테고리, 해시태그를 입력해 여행을 생성할 수 있다.")
    @Test
    void create() {
        assertThatCode(() -> Trip.create(
                memberId,
                destinationId,
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5)),
                true,
                4,
                List.of("속초 여행"),
                TripCategory.DOMESTIC
        )).doesNotThrowAnyException();
    }

    @DisplayName("제목, 여행지, 기간, 공개 여부를 입력해 여행과 일정 목록을 생성할 수 있다.")
    @Test
    void createWithItinerary() {
        assertThatCode(() -> Trip.createWithItineraries(
                memberId,
                destinationId,
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5)),
                true,
                4,
                List.of("속초 여행"),
                TripCategory.DOMESTIC
        )).doesNotThrowAnyException();
    }

    @Test
    void 여행을_생성하면_생성자는_해당_여행에_리더가_된다() {
        // given
        Trip trip = Trip.create(
                memberId,
                destinationId,
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5)),
                true,
                4,
                List.of("속초 여행"),
                TripCategory.DOMESTIC
        );

        // when
        List<TripParticipant> participants = trip.getTripParticipants().getValues();

        // then
        assertAll(
                () -> assertThat(participants).hasSize(1),
                () -> assertThat(participants.get(0).getRole()).isEqualTo(ParticipantRole.LEADER),
                () -> assertThat(participants.get(0).getMemberId()).isEqualTo(memberId)
        );
    }

    @Test
    void 여행_일정이_없다면_균등_일정_생성으로_업데이트가_가능하다() {
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(6));
        Trip trip = Trip.create(
                memberId,
                UUID.randomUUID(),
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                period,
                true,
                4,
                List.of("Test Tag"),
                TripCategory.DOMESTIC
        );
        //when
        TripPeriod updateTripPeriod = new TripPeriod(LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));
        trip.updatePeriod(updateTripPeriod, memberId);


        //then
        assertThat(trip.getPeriod().getStart()).isEqualTo(updateTripPeriod.getStart());
        assertThat(trip.getPeriod().getEnd()).isEqualTo(updateTripPeriod.getEnd());
        assertThat(trip.getItineraries().getValues().size()).isEqualTo(3);
        assertThat(trip.getItineraries().getValues().get(0).getName()).isEqualTo("day 1");
    }

    @Test
    void 여행_일정은_리더만_수정_가능하다() {
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(6));
        Trip trip = Trip.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                period,
                true,
                4,
                List.of("Test Tag"),
                TripCategory.DOMESTIC
        );
        trip.addParticipant(TripParticipant.createTripParticipant(memberId, trip));

        //when
        TripPeriod updateTripPeriod = new TripPeriod(LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));

        //then
        assertThrows(PeriodUpdateFailedException.class, () -> trip.updatePeriod(updateTripPeriod, memberId));
    }
}
