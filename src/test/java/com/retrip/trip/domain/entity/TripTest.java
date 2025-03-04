package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class TripTest {
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID participantId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c004");
    UUID destinationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");

    @DisplayName("제목, 설명, 여행지, 기간, 공개 여부, 참가인원수,카테고리를 입력해 여행을 생성할 수 있다.")
    @Test
    void create() {
        assertThatCode(() -> Trip.create(
                memberId,
                destinationId,
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(
                        LocalDate.of(2025, 3, 10),
                        LocalDate.of(2025, 3, 15)),
                true,
                4,
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
                        LocalDate.of(2025, 3, 10),
                        LocalDate.of(2025, 3, 15)),
                true,
                4,
                TripCategory.DOMESTIC
        )).doesNotThrowAnyException();
    }

    @DisplayName("리더는 여행 일자와 여행 기간을 수정할 수 있다.")
    @Test
    void updatePeriodWithItineraryByLeader() {
        Trip trip = Trip.create(
                memberId,
                destinationId,
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(
                        LocalDate.of(2025, 3, 4),
                        LocalDate.of(2025, 3, 6)),
                true,
                4,
                TripCategory.DOMESTIC
        );

        assertThatCode(() -> trip.updatePeriodWithItineraries(
                new TripPeriod(
                        LocalDate.of(2025, 3, 10),
                        LocalDate.of(2025, 3, 20)
                ), List.of(
                        LocalDate.of(2025, 3, 11),
                        LocalDate.of(2025, 3, 12)),
                memberId
        )).doesNotThrowAnyException();
    }

    @DisplayName("사용자는 여행 일자와 여행 기간을 수정할 수 없다.")
    @Test
    void updatePeriodWithItineraryByParticipant() {
        Trip trip = Trip.create(
                memberId,
                destinationId,
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(
                        LocalDate.of(2025, 3, 4),
                        LocalDate.of(2025, 3, 6)),
                true,
                4,
                TripCategory.DOMESTIC
        );

        //todo: 추후 참여자 로직 생성시, 변경 필요
        TripParticipant participant = TripParticipant.createTripParticipant(participantId, trip);
        trip.getParticipants().getValues().add(participant);


        assertThatThrownBy(() -> trip.updatePeriodWithItineraries(
                new TripPeriod(
                        LocalDate.of(2025, 3, 10),
                        LocalDate.of(2025, 3, 20)
                ), List.of(
                        LocalDate.of(2025, 3, 11),
                        LocalDate.of(2025, 3, 12)),
                participantId
        )).isExactlyInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("리더는 여행 기간을 수정할 수 있다.")
    @Test
    void updateItineraryByLeader() {
        Trip trip = Trip.createWithItineraries(
                memberId,
                destinationId,
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(
                        LocalDate.of(2025, 3, 4),
                        LocalDate.of(2025, 3, 6)),
                true,
                4,
                TripCategory.DOMESTIC
        );

        assertThatCode(() -> trip.updatePeriodWithItineraries(
                new TripPeriod(
                        LocalDate.of(2025, 3, 10),
                        LocalDate.of(2025, 3, 20)
                ), List.of(
                        LocalDate.of(2025, 3, 11),
                        LocalDate.of(2025, 3, 12)),
                memberId
        )).doesNotThrowAnyException();
    }

    @DisplayName("사용자는 여행 일자와 여행 기간을 수정할 수 없다.")
    @Test
    void updateItineraryByParticipant() {
        Trip trip = Trip.createWithItineraries(
                memberId,
                destinationId,
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(
                        LocalDate.of(2025, 3, 4),
                        LocalDate.of(2025, 3, 6)),
                true,
                4,
                TripCategory.DOMESTIC
        );

        //todo: 추후 참여자 로직 생성시, 변경 필요
        TripParticipant participant = TripParticipant.createTripParticipant(participantId, trip);
        trip.getParticipants().getValues().add(participant);


        assertThatThrownBy(() -> trip.updatePeriodWithItineraries(
                new TripPeriod(
                        LocalDate.of(2025, 3, 10),
                        LocalDate.of(2025, 3, 20)
                ), List.of(
                        LocalDate.of(2025, 3, 11),
                        LocalDate.of(2025, 3, 12)),
                participantId
        )).isExactlyInstanceOf(IllegalArgumentException.class);
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
                        LocalDate.of(2025, 3, 10),
                        LocalDate.of(2025, 3, 15)),
                true,
                4,
                TripCategory.DOMESTIC
        );

        // when
        List<TripParticipant> participants = trip.getParticipants().getValues();

        // then
        assertAll(
                () -> assertThat(participants).hasSize(1),
                () -> assertThat(participants.get(0).getRole()).isEqualTo(ParticipantRole.LEADER),
                () -> assertThat(participants.get(0).getUserId()).isEqualTo(memberId)
        );
    }


}
