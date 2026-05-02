package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.PeriodUpdateFailedException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.fixture.TripFixture;
import com.retrip.trip.domain.vo.HashTagInfo;
import com.retrip.trip.domain.vo.ParticipantRole;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripStatus;
import com.retrip.trip.domain.vo.TripTitle;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.UUID;

import static com.retrip.trip.domain.fixture.TripFixture.LEADER_ID;
import static com.retrip.trip.domain.fixture.TripFixture.TRIP_ID;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TripTest {
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID destinationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");

    List<UUID> destinationIds = List.of(destinationId);
    List<HashTagInfo> hashTags = List.of(new HashTagInfo("속초 여행", 1));
    List<HashTagInfo> testHashTags = List.of(new HashTagInfo("Test Tag", 1));

    @DisplayName("제목, 설명, 여행지, 기간, 공개 여부, 참가인원수,카테고리, 해시태그를 입력해 여행을 생성할 수 있다.")
    @Test
    void create() {
        assertThatCode(() -> Trip.create(
                memberId,
                destinationIds,
                new TripTitle("속초 여행 멤버 구함"),
                "https://image.url",
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5)),
                true,
                4,
                hashTags,
                TripCategory.DOMESTIC,
                TripStatus.RECRUITING)).doesNotThrowAnyException();
    }

    @DisplayName("제목, 여행지, 기간, 공개 여부를 입력해 여행과 일정 목록을 생성할 수 있다.")
    @Test
    void createWithItinerary() {
        assertThatCode(() -> Trip.createWithItineraries(
                memberId,
                destinationIds,
                new TripTitle("속초 여행 멤버 구함"),
                "https://image.url",
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5)),
                true,
                4,
                hashTags,
                TripCategory.DOMESTIC,
                TripStatus.RECRUITING
        )).doesNotThrowAnyException();
    }

    @Test
    void 여행을_생성하면_생성자는_해당_여행에_리더가_된다() {
        // given
        Trip trip = Trip.create(
                memberId,
                destinationIds,
                new TripTitle("속초 여행 멤버 구함"),
                "https://image.url",
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5)),
                true,
                4,
                hashTags,
                TripCategory.DOMESTIC,
                TripStatus.RECRUITING);

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
                destinationIds,
                new TripTitle("속초 여행 멤버 구함"),
                "https://image.url",
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                period,
                true,
                4,
                testHashTags,
                TripCategory.DOMESTIC,
                TripStatus.RECRUITING);
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
    void 모집완료_상태에서_여행중으로_상태를_변경할_수_있다() {
        // given
        Trip trip = TripFixture.createTrip(TRIP_ID);
        ReflectionTestUtils.setField(trip, "status", TripStatus.RECRUITMENT_CLOSED);

        // when
        trip.changeStatusToInProgress();

        // then
        assertThat(trip.getStatus()).isEqualTo(TripStatus.IN_PROGRESS);
    }

    @Test
    void 모집중_상태에서_여행중으로_상태를_변경할_수_있다() {
        // given
        Trip trip = TripFixture.createTrip(TRIP_ID);

        // when
        trip.changeStatusToInProgress();

        // then
        assertThat(trip.getStatus()).isEqualTo(TripStatus.IN_PROGRESS);
    }

    @Test
    void 여행후_상태에서는_여행중으로_변경할_수_없다() {
        // given
        Trip trip = TripFixture.createTrip(TRIP_ID);
        ReflectionTestUtils.setField(trip, "status", TripStatus.COMPLETED);

        // when & then
        assertThatThrownBy(trip::changeStatusToInProgress)
                .isExactlyInstanceOf(BusinessException.class);
    }

    @Test
    void 여행중_상태에서_여행후로_상태를_변경할_수_있다() {
        // given
        Trip trip = TripFixture.createTrip(TRIP_ID);
        ReflectionTestUtils.setField(trip, "status", TripStatus.IN_PROGRESS);

        // when
        trip.changeStatusToCompleted();

        // then
        assertThat(trip.getStatus()).isEqualTo(TripStatus.COMPLETED);
    }

    @Test
    void 여행중이_아닌_상태에서는_여행후로_변경할_수_없다() {
        // given
        Trip trip = TripFixture.createTrip(TRIP_ID);
        // RECRUITMENT_CLOSED 상태

        // when & then
        assertThatThrownBy(trip::changeStatusToCompleted)
                .isExactlyInstanceOf(BusinessException.class);
    }

    @Test
    void 여행_시작일이_지난_경우_모집완료에서_모집중으로_되돌릴_수_없다() {
        // given
        Trip trip = TripFixture.createTrip(TRIP_ID);
        ReflectionTestUtils.setField(trip, "status", TripStatus.RECRUITMENT_CLOSED);
        ReflectionTestUtils.setField(trip.getPeriod(), "start", LocalDate.now().minusDays(1));

        // when & then
        assertThatThrownBy(trip::toggleRecruitmentStatus)
                .isExactlyInstanceOf(BusinessException.class);
    }

    @Test
    void 여행_시작일이_오늘이면_모집완료에서_모집중으로_되돌릴_수_없다() {
        // given
        Trip trip = TripFixture.createTrip(TRIP_ID);
        ReflectionTestUtils.setField(trip, "status", TripStatus.RECRUITMENT_CLOSED);
        ReflectionTestUtils.setField(trip.getPeriod(), "start", LocalDate.now());

        // when & then
        assertThatThrownBy(trip::toggleRecruitmentStatus)
                .isExactlyInstanceOf(BusinessException.class);
    }

    @Test
    void 여행_시작일이_미래이면_모집완료에서_모집중으로_되돌릴_수_있다() {
        // given
        Trip trip = TripFixture.createTrip(TRIP_ID); // period.start = now + 1일

        tripManuallyClosedRecruitment(trip);

        // when & then
        assertThatCode(trip::toggleRecruitmentStatus).doesNotThrowAnyException();
        assertThat(trip.getStatus()).isEqualTo(TripStatus.RECRUITING);
    }

    private void tripManuallyClosedRecruitment(Trip trip) {
        ReflectionTestUtils.setField(trip, "status", TripStatus.RECRUITMENT_CLOSED);
    }

    @Test
    void 여행_일정은_리더만_수정_가능하다() {
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(6));
        Trip trip = Trip.create(
                UUID.randomUUID(),
                destinationIds,
                new TripTitle("속초 여행 멤버 구함"),
                "https://image.url",
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                period,
                true,
                4,
                testHashTags,
                TripCategory.DOMESTIC,
                TripStatus.RECRUITING);
        trip.addParticipant(TripParticipant.createTripParticipant(memberId, trip));

        //when
        TripPeriod updateTripPeriod = new TripPeriod(LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));

        //then
        assertThrows(PeriodUpdateFailedException.class, () -> trip.updatePeriod(updateTripPeriod, memberId));
    }
}
