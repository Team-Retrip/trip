package com.retrip.trip.application.in;

import com.retrip.trip.application.in.base.BaseTripServiceTest;
import com.retrip.trip.application.in.request.*;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.domain.entity.participant.Participant;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripDemand;
import com.retrip.trip.domain.exception.*;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.fixture.TripFixture;
import com.retrip.trip.domain.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TripServiceTest extends BaseTripServiceTest {
    private TripPeriod createFuturePeriod() {
        return new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
    }

    private Trip createTestTrip(String title, String description, TripCategory category) {
        TripPeriod period = createFuturePeriod();
        Trip trip =
                Trip.create(
                        UUID.randomUUID(),
                        new TripTitle(title),
                        new TripDescription(description),
                        period,
                        true,
                        4,
                        category);
        return tripRepository.save(trip);
    }

    private Trip createReadyTrip(UUID leaderId) {
        Trip trip =
                Trip.create(
                        locationId,
                        new TripTitle("준비된 여행"),
                        new TripDescription("설명"),
                        new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)),
                        true,
                        4,
                        TripCategory.DOMESTIC);
        ReflectionTestUtils.setField(trip, "status", TripStatus.BEFORE_TRIP);
        return tripRepository.save(trip);
    }

    private Trip createProgressTrip(UUID leaderId) {
        Trip trip =
                Trip.create(
                        locationId,
                        new TripTitle("진행중 여행"),
                        new TripDescription("설명"),
                        new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)),
                        true,
                        4,
                        TripCategory.DOMESTIC);
        ReflectionTestUtils.setField(trip, "status", TripStatus.IN_PROGRESS);
        return tripRepository.save(trip);
    }

    @Test
    void 여행을_생성_한다() {
        TripCreateRequest request =
                new TripCreateRequest(
                        memberId,
                        locationId,
                        "속초 여행 멤버 구함",
                        "속초 여행은 이렇게이렇게 갈겁니다~",
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5),
                        true,
                        4,
                        TripCategory.DOMESTIC);
        TripCreateResponse response = tripService.createTrip(request);
        assertThat(response.id()).isNotNull();
        assertThat(response.destinationId()).isEqualTo(locationId);
    }

    @Test
    void 여행_목록을_조회한다() {
        TripPeriod period = createFuturePeriod();
        tripRepository.save(
                Trip.createWithItineraries(
                        UUID.randomUUID(),
                        new TripTitle("속초 여행 멤버 구함"),
                        new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));
        tripRepository.save(
                Trip.createWithItineraries(
                        UUID.randomUUID(),
                        new TripTitle("강릉 여행 멤버 구함"),
                        new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));
        tripRepository.save(
                Trip.createWithItineraries(
                        UUID.randomUUID(),
                        new TripTitle("대구 여행 멤버 구함"),
                        new TripDescription("대구 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));
        tripRepository.save(
                Trip.createWithItineraries(
                        UUID.randomUUID(),
                        new TripTitle("부산 여행 멤버 구함"),
                        new TripDescription("부산 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));

        Page<TripResponse> trips = tripService.getTrips(PageRequest.of(0, 2));
        assertThat(trips.getTotalElements()).isEqualTo(2);
        assertThat(trips.getPageable().getOffset()).isEqualTo(0);
        assertThat(trips.getPageable().getPageSize()).isEqualTo(2);
    }

    @Test
    void 사용자가_참여_요청을_보낸다() {
        // given
        Trip trip = createTestTrip("테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        TripDemandRequest request = new TripDemandRequest(newMemberId, "참여 요청 메시지");

        // when
        TripDemandResponse response = tripService.tripDemand(trip.getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.tripId()).isEqualTo(trip.getId());
        assertThat(response.memberId()).isEqualTo(newMemberId);
        assertThat(response.status()).isEqualTo("대기");
    }

    @Test
    void 리더가_참여_요청을_승인하면_실제_참여자로_등록된다() {
        // given
        Trip newTrip =
                TripFixture.createTestTrip(memberId, "승인 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        TripDemand tripDemand = TripDemand.create(newMemberId, newTrip, "참여 요청 메시지");
        newTrip.getTripDemands().getValues().add(tripDemand);
        tripRepository.save(newTrip);

        // then
        TripDemandApproveResponse response =
                tripService.approve(memberId, newTrip.getId(), tripDemand.getId());
        Trip trip = tripRepository.findById(newTrip.getId()).orElseThrow();
        UUID newParticipantMemberId =
                trip.getParticipants().getValues().stream()
                        .map(Participant::getMemberId)
                        .filter(id -> id.equals(newMemberId))
                        .findFirst()
                        .orElseThrow();

        // when
        assertThat(response).isNotNull();
        assertThat(newParticipantMemberId).isEqualTo(newMemberId);
        assertThat(response.statusCode()).isEqualTo(TripDemandStatus.APPROVED.getCode());
    }

    @Test
    void 리더가_아니면_참여_요청을_승인할_수_없다() {
        // given
        Trip newTrip =
                TripFixture.createTestTrip(memberId, "승인 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        TripDemand tripDemand = TripDemand.create(newMemberId, newTrip, "참여 요청 메시지");
        newTrip.getTripDemands().getValues().add(tripDemand);
        tripRepository.save(newTrip);
        tripService.approve(memberId, newTrip.getId(), tripDemand.getId());

        // then && when
        assertThrows(
                BusinessException.class,
                () -> tripService.approve(newMemberId, newTrip.getId(), tripDemand.getId()));
    }

    @Test
    void 리더가_참여_요청을_거절하면_요청_상태가_거절로_변경된다() {
        // given
        Trip newTrip =
                TripFixture.createTestTrip(memberId, "거절 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        TripDemand tripDemand = TripDemand.create(newMemberId, newTrip, "참여 요청 메시지");
        newTrip.getTripDemands().getValues().add(tripDemand);
        tripRepository.save(newTrip);

        // then
        TripDemandRejectResponse response =
                tripService.reject(memberId, newTrip.getId(), tripDemand.getId());

        // when
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(TripDemandStatus.REJECTED.getCode());
    }

    @Test
    void 리더가_아니면_참여_요청을_거절할_수_없다() {
        // given
        Trip newTrip =
                TripFixture.createTestTrip(memberId, "거절 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        TripDemand tripDemand = TripDemand.create(newMemberId, newTrip, "참여 요청 메시지");
        newTrip.getTripDemands().getValues().add(tripDemand);
        tripRepository.save(newTrip);
        tripService.approve(memberId, newTrip.getId(), tripDemand.getId());

        // then && when
        assertThrows(
                BusinessException.class,
                () -> tripService.reject(newMemberId, newTrip.getId(), tripDemand.getId()));
    }

    @Test
    void 빈_여행_일정을_수정한다() {
        // given
        Trip trip = createTestTrip("테스트 여행", "여행 설명", TripCategory.DOMESTIC);

        // then
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);
        PeriodUpdateRequest request = TripRequestFixture.createPeriod(memberId, start, end);
        PeriodUpdateResponse response = tripService.updatePeriod(trip.getId(), request);

        // when
        assertThat(response.start()).isEqualTo(start);
        assertThat(response.end()).isEqualTo(end);
        assertThat(response.itineraries().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 시작 전 ~ 이전 일정 시작 전'으로 변경할 수 있다")
    void updatePeriodIsBeforePrePeriodStart() {
        // given
        TripPeriod period =
                new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Trip trip =
                tripRepository.save(
                        Trip.createWithItineraries(
                                UUID.randomUUID(),
                                new TripTitle("강릉 여행 멤버 구함"),
                                new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                                period,
                                true,
                                4,
                                TripCategory.DOMESTIC));

        // when
        PeriodUpdateRequest request =
                TripRequestFixture.createPeriod(
                        memberId, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        PeriodUpdateResponse response = tripService.updatePeriod(trip.getId(), request);

        // then
        assertThat(response.itineraries()).hasSize(3);
        assertThat(
                        response.itineraries().stream()
                                .map(PeriodUpdateResponse.ItineraryUpdateResponse::name)
                                .toList())
                .containsExactly("day 1", "day 2", "day 3");
        assertThat(
                        response.itineraries().stream()
                                .map(PeriodUpdateResponse.ItineraryUpdateResponse::date)
                                .toList())
                .containsExactly(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2),
                        LocalDate.now().plusDays(3));
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 시작 전 ~ 이전 일정 종료 전'으로 변경할 수 있다")
    void updatePeriodBetweenPrePeriodStartBeforeAndPrePeriodEndBefore() {
        // given
        TripPeriod period =
                new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Trip trip =
                tripRepository.save(
                        Trip.createWithItineraries(
                                UUID.randomUUID(),
                                new TripTitle("강릉 여행 멤버 구함"),
                                new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                                period,
                                true,
                                4,
                                TripCategory.DOMESTIC));

        // when
        PeriodUpdateRequest request =
                TripRequestFixture.createPeriod(
                        memberId, LocalDate.now().plusDays(3), LocalDate.now().plusDays(8));
        PeriodUpdateResponse response = tripService.updatePeriod(trip.getId(), request);

        // then
        assertThat(response.itineraries()).hasSize(6);
        assertThat(
                        response.itineraries().stream()
                                .map(PeriodUpdateResponse.ItineraryUpdateResponse::name)
                                .toList())
                .containsExactly("day 1", "day 2", "day 3", "day 4", "day 5", "day 6");
        assertThat(
                        response.itineraries().stream()
                                .map(PeriodUpdateResponse.ItineraryUpdateResponse::date)
                                .toList())
                .containsExactly(
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(4),
                        LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(6),
                        LocalDate.now().plusDays(7),
                        LocalDate.now().plusDays(8));
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 시작 전 ~ 이전 일정 종료 후'으로 변경할 수 있다")
    void updatePeriodBetweenPrePeriodStartBeforeAndPrePeriodEndAfter() {
        // given
        TripPeriod period =
                new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));
        Trip trip =
                tripRepository.save(
                        Trip.createWithItineraries(
                                UUID.randomUUID(),
                                new TripTitle("강릉 여행 멤버 구함"),
                                new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                                period,
                                true,
                                4,
                                TripCategory.DOMESTIC));

        // when

        PeriodUpdateRequest request =
                TripRequestFixture.createPeriod(
                        memberId, LocalDate.now().plusDays(3), LocalDate.now().plusDays(8));
        PeriodUpdateResponse response = tripService.updatePeriod(trip.getId(), request);

        // then
        assertThat(response.itineraries()).hasSize(6);
        assertThat(
                        response.itineraries().stream()
                                .map(PeriodUpdateResponse.ItineraryUpdateResponse::name)
                                .toList())
                .containsExactly("day 1", "day 2", "day 3", "day 4", "day 5", "day 6");
        assertThat(
                        response.itineraries().stream()
                                .map(PeriodUpdateResponse.ItineraryUpdateResponse::date)
                                .toList())
                .containsExactly(
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(4),
                        LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(6),
                        LocalDate.now().plusDays(7),
                        LocalDate.now().plusDays(8));
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 시작 후 ~ 이전 일정 종료 전'으로 변경할 수 있다")
    void updatePeriodBetweenPrePeriodStartAfterAndPrePeriodEndBefore() {
        // given
        TripPeriod period =
                new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Trip trip =
                tripRepository.save(
                        Trip.createWithItineraries(
                                UUID.randomUUID(),
                                new TripTitle("강릉 여행 멤버 구함"),
                                new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                                period,
                                true,
                                4,
                                TripCategory.DOMESTIC));

        // when
        PeriodUpdateRequest request =
                TripRequestFixture.createPeriod(
                        memberId, LocalDate.now().plusDays(7), LocalDate.now().plusDays(9));
        PeriodUpdateResponse response = tripService.updatePeriod(trip.getId(), request);

        // then
        assertThat(response.itineraries()).hasSize(3);
        assertThat(
                        response.itineraries().stream()
                                .map(PeriodUpdateResponse.ItineraryUpdateResponse::name)
                                .toList())
                .containsExactly("day 1", "day 2", "day 3");
        assertThat(
                        response.itineraries().stream()
                                .map(PeriodUpdateResponse.ItineraryUpdateResponse::date)
                                .toList())
                .containsExactly(
                        LocalDate.now().plusDays(7),
                        LocalDate.now().plusDays(8),
                        LocalDate.now().plusDays(9));
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 시작 후 ~ 이전 일정 종료 후'으로 변경할 수 있다")
    void updatePeriodBetweenPrePeriodStartAfterAndPrePeriodEndAfter() {
        // given
        TripPeriod period =
                new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Trip trip =
                tripRepository.save(
                        Trip.createWithItineraries(
                                UUID.randomUUID(),
                                new TripTitle("강릉 여행 멤버 구함"),
                                new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                                period,
                                true,
                                4,
                                TripCategory.DOMESTIC));

        // when
        PeriodUpdateRequest request =
                TripRequestFixture.createPeriod(
                        memberId, LocalDate.now().plusDays(7), LocalDate.now().plusDays(12));
        PeriodUpdateResponse response = tripService.updatePeriod(trip.getId(), request);

        // then
        assertThat(response.itineraries()).hasSize(6);
        assertThat(
                        response.itineraries().stream()
                                .map(PeriodUpdateResponse.ItineraryUpdateResponse::name)
                                .toList())
                .containsExactly("day 1", "day 2", "day 3", "day 4", "day 5", "day 6");
        assertThat(
                        response.itineraries().stream()
                                .map(PeriodUpdateResponse.ItineraryUpdateResponse::date)
                                .toList())
                .containsExactly(
                        LocalDate.now().plusDays(7),
                        LocalDate.now().plusDays(8),
                        LocalDate.now().plusDays(9),
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(11),
                        LocalDate.now().plusDays(12));
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 종료 후'으로 변경할 수 있다")
    void updatePeriodIsAfterPrePeriodEndAfter() {

        // given
        TripPeriod period =
                new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Trip trip =
                tripRepository.save(
                        Trip.createWithItineraries(
                                UUID.randomUUID(),
                                new TripTitle("강릉 여행 멤버 구함"),
                                new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                                period,
                                true,
                                4,
                                TripCategory.DOMESTIC));

        // when
        PeriodUpdateRequest request =
                TripRequestFixture.createPeriod(
                        memberId, LocalDate.now().plusDays(11), LocalDate.now().plusDays(14));
        PeriodUpdateResponse response = tripService.updatePeriod(trip.getId(), request);

        // then
        assertThat(response.itineraries()).hasSize(4);
        assertThat(
                        response.itineraries().stream()
                                .map(PeriodUpdateResponse.ItineraryUpdateResponse::name)
                                .toList())
                .containsExactly("day 1", "day 2", "day 3", "day 4");
        assertThat(
                        response.itineraries().stream()
                                .map(PeriodUpdateResponse.ItineraryUpdateResponse::date)
                                .toList())
                .containsExactly(
                        LocalDate.now().plusDays(11),
                        LocalDate.now().plusDays(12),
                        LocalDate.now().plusDays(13),
                        LocalDate.now().plusDays(14));
    }

    @Test
    @DisplayName("멤버가 성공적으로 여행을 나간다")
    void leaveTrip_success_forMember() {
        // given
        Trip trip = createReadyTrip(memberId);
        trip.addParticipant(Participant.createTripParticipant(newMemberId, trip));
        tripRepository.save(trip);

        // when
        tripService.leaveTrip(trip.getId(), newMemberId);

        // then
        Trip updatedTrip = tripRepository.findById(trip.getId()).get();
        boolean isParticipantPresent =
                updatedTrip.getParticipants().findParticipantById(newMemberId).isPresent();
        assertThat(isParticipantPresent).isFalse();
    }

    @Test
    @DisplayName("리더는 위임 없이 여행을 나갈 수 없다")
    void leaveTrip_fail_forLeader() {
        // given
        Trip trip = createReadyTrip(memberId);

        // when & then
        assertThrows(
                LeaderCannotLeaveException.class,
                () -> {
                    tripService.leaveTrip(trip.getId(), memberId);
                });
    }

    @Test
    @DisplayName("여행이 '여행 전' 상태가 아니면 나갈 수 없다")
    void leaveTrip_fail_whenTripNotReady() {
        // given
        Trip trip = createProgressTrip(memberId);
        trip.addParticipant(Participant.createTripParticipant(newMemberId, trip));
        tripRepository.save(trip);

        // when & then
        assertThrows(
                TripNotReadyException.class,
                () -> {
                    tripService.leaveTrip(trip.getId(), newMemberId);
                });
    }

    @Test
    @DisplayName("리더가 성공적으로 다른 멤버에게 리더를 위임한다")
    void delegateLeader_success() {
        // given
        Trip trip = createReadyTrip(memberId);
        trip.addParticipant(Participant.createTripParticipant(newMemberId, trip));
        tripRepository.save(trip);
        DelegateLeaderRequest request = new DelegateLeaderRequest(memberId, newMemberId);

        // when
        tripService.delegateLeader(trip.getId(), request);

        // then
        Trip updatedTrip = tripRepository.findById(trip.getId()).get();
        assertTrue(updatedTrip.getParticipants().findParticipantById(newMemberId).get().isLeader());
        assertThat(updatedTrip.getParticipants().findParticipantById(memberId).get().isLeader())
                .isFalse();
    }

    @Test
    @DisplayName("리더가 아닌 멤버는 리더를 위임할 수 없다")
    void delegateLeader_fail_notLeader() {
        // given
        Trip trip = createReadyTrip(memberId);
        trip.addParticipant(Participant.createTripParticipant(newMemberId, trip));
        tripRepository.save(trip);
        DelegateLeaderRequest request = new DelegateLeaderRequest(newMemberId, memberId);

        // when & then
        assertThrows(
                NotLeaderException.class,
                () -> {
                    tripService.delegateLeader(trip.getId(), request);
                });
    }

    @Test
    @DisplayName("리더는 자기 자신에게 리더를 위임할 수 없다")
    void delegateLeader_fail_toSelf() {
        // given
        Trip trip = createReadyTrip(memberId);
        DelegateLeaderRequest request = new DelegateLeaderRequest(memberId, memberId);

        // when & then
        assertThrows(
                InvalidValueException.class,
                () -> {
                    tripService.delegateLeader(trip.getId(), request);
                });
    }

    @Test
    @DisplayName("참여자가 아닌 사람에게 리더를 위임할 수 없다")
    void delegateLeader_fail_toNonParticipant() {
        // given
        Trip trip = createReadyTrip(memberId);
        UUID nonParticipantId = UUID.randomUUID();
        DelegateLeaderRequest request = new DelegateLeaderRequest(memberId, nonParticipantId);

        // when & then
        assertThrows(
                NotParticipantException.class,
                () -> {
                    tripService.delegateLeader(trip.getId(), request);
                });
    }

    @Test
    @DisplayName("여행을 나간 후 '나의 여행 목록'에 보이지 않는다")
    void getMyTrips_afterLeaving() {
        // given
        Trip trip = createReadyTrip(memberId);
        trip.addParticipant(Participant.createTripParticipant(newMemberId, trip));
        tripRepository.save(trip);

        // when
        tripService.leaveTrip(trip.getId(), newMemberId);
        Page<MyTripResponse> myTrips = tripService.getMyTrips(newMemberId, PageRequest.of(0, 10));

        // then
        assertThat(myTrips.getTotalElements()).isZero();
    }

    @Test
    void 리더는_참여자들을_추방할_수_있다() {
        // given
        Trip newTrip =
                TripFixture.createTestTrip(memberId, "승인 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        TripDemand tripDemand = TripDemand.create(newMemberId, newTrip, "참여 요청 메시지");
        newTrip.getTripDemands().getValues().add(tripDemand);
        tripRepository.save(newTrip);
        TripDemandApproveResponse response =
                tripService.approve(memberId, newTrip.getId(), tripDemand.getId());

        // then
        tripService.banMembers(memberId, newTrip.getId(), List.of(newMemberId));

        Trip trip = tripRepository.findById(newTrip.getId()).orElseThrow();
        Participant banParticipant =
                trip.getParticipants().getValues().stream()
                        .filter(participant -> participant.getMemberId().equals(newMemberId))
                        .findFirst()
                        .orElseThrow();

        // when
        assertThat(response).isNotNull();
        assertThat(banParticipant.getStatus()).isEqualTo(ParticipantStatus.EXPELLED);
    }

    @Test
    void 해당_여행에_강퇴당한_사용자는_다시_참여요청할_수_없다() {
        // given
        Trip newTrip =
                TripFixture.createTestTrip(memberId, "승인 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        TripDemand tripDemand = TripDemand.create(newMemberId, newTrip, "참여 요청 메시지");
        newTrip.getTripDemands().getValues().add(tripDemand);
        tripRepository.save(newTrip);
        tripService.approve(memberId, newTrip.getId(), tripDemand.getId());
        tripService.banMembers(memberId, newTrip.getId(), List.of(newMemberId));

        TripDemandRequest request = new TripDemandRequest(newMemberId, "강퇴당한 후 다시 참여 요청 메시지");

        // when && then
        assertThrows(
                BusinessException.class, () -> tripService.tripDemand(newTrip.getId(), request));
    }

    @Test
    @DisplayName("여행이 가득 찼을 경우, 새로운 사용자는 참여 요청을 할 수 없다.")
    void tripIsFull_then_cannotJoin() {
        // given
        Trip trip =
                Trip.create(
                        UUID.randomUUID(),
                        new TripTitle("꽉 찬 여행"),
                        new TripDescription("더 이상 자리가 없어요"),
                        new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)),
                        true,
                        1,
                        TripCategory.DOMESTIC);
        tripRepository.save(trip);

        // when & then
        TripDemandRequest newRequest = new TripDemandRequest(UUID.randomUUID(), "저도 참여하고 싶어요!");

        assertThrows(
                ParticipantFullException.class,
                () -> {
                    tripService.tripDemand(trip.getId(), newRequest);
                });
    }

    @Test
    @DisplayName("여행 리더는 최대 참여 인원을 변경할 수 있다.")
    void leader_can_update_maxParticipants() {
        // given
        Trip trip =
                Trip.create(
                        UUID.randomUUID(),
                        new TripTitle("인원 변경 테스트"),
                        new TripDescription("설명"),
                        new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)),
                        true,
                        3,
                        TripCategory.DOMESTIC);
        tripRepository.save(trip);

        // when
        int newMaxParticipants = 5;
        trip.getParticipants().updateMaxParticipants(newMaxParticipants, memberId);
        tripRepository.save(trip);

        // then
        Trip updatedTrip = tripRepository.findById(trip.getId()).get();
        assertThat(updatedTrip.getMaxParticipants()).isEqualTo(newMaxParticipants);
    }

    @Test
    @DisplayName("리더가 아닌 멤버는 최대 참여 인원을 변경할 수 없다.")
    void nonLeader_cannot_update_maxParticipants() {
        // given
        UUID nonLeaderId = UUID.randomUUID();
        Trip trip =
                Trip.create(
                        UUID.randomUUID(),
                        new TripTitle("권한 테스트"),
                        new TripDescription("설명"),
                        new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)),
                        true,
                        3,
                        TripCategory.DOMESTIC);
        trip.addParticipant(Participant.createTripParticipant(nonLeaderId, trip));
        tripRepository.save(trip);

        // when & then
        assertThrows(
                NotLeaderException.class,
                () -> {
                    trip.getParticipants().updateMaxParticipants(5, nonLeaderId);
                });
    }

    @Test
    @DisplayName("최대 참여 인원을 현재 참여 인원보다 적게 변경할 수 없다.")
    void cannot_update_maxParticipants_lessThan_currentParticipants() {
        // given
        Trip trip =
                Trip.create(
                        UUID.randomUUID(),
                        new TripTitle("인원 축소 테스트"),
                        new TripDescription("설명"),
                        new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)),
                        true,
                        4,
                        TripCategory.DOMESTIC);
        trip.addParticipant(Participant.createTripParticipant(UUID.randomUUID(), trip));
        trip.addParticipant(Participant.createTripParticipant(UUID.randomUUID(), trip));
        tripRepository.save(trip);

        // when & then
        assertThrows(
                InvalidValueException.class,
                () -> {
                    trip.getParticipants().updateMaxParticipants(2, memberId);
                });
    }

    @Test
    @DisplayName("isLeader 메서드가 정확하게 리더와 멤버를 구분하는지 확인한다.")
    void isLeader_check_works_correctly() {
        // given
        UUID nonLeaderId = UUID.randomUUID();
        Trip trip =
                Trip.create(
                        UUID.randomUUID(),
                        new TripTitle("isLeader 테스트"),
                        new TripDescription("설명"),
                        new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)),
                        true,
                        3,
                        TripCategory.DOMESTIC);
        trip.addParticipant(Participant.createTripParticipant(nonLeaderId, trip));
        tripRepository.save(trip);

        // when
        Trip savedTrip = tripRepository.findById(trip.getId()).get();
        boolean isLeaderResult = savedTrip.getParticipants().isLeader(memberId);
        boolean isNotLeaderResult = savedTrip.getParticipants().isLeader(nonLeaderId);

        // then
        assertTrue(isLeaderResult);
        assertThrows(
                InvalidValueException.class,
                () -> {
                    savedTrip.getParticipants().isLeader(UUID.randomUUID());
                });
    }
}
