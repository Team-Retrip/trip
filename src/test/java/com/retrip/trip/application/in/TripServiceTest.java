package com.retrip.trip.application.in;

import com.retrip.trip.application.in.base.BaseTripServiceTest;
import com.retrip.trip.application.in.request.*;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.response.TripDetailResponse.TripParticipantResponse;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripConfirmationDemand;
import com.retrip.trip.domain.entity.TripConfirmationReply;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.exception.LeaderCannotLeaveException;
import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.NotParticipantException;
import com.retrip.trip.domain.exception.TripNotReadyException;
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

import static com.retrip.trip.domain.fixture.TripFixture.정수_ID;
import static com.retrip.trip.domain.vo.TripPassword.PASSWORD_MIN_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TripServiceTest extends BaseTripServiceTest {

    private static final String TEST_IMAGE_URL = "https://test-image.com/default.jpg";

    private Trip createTestTripWithParticipants(TripStatus status) {
        Trip trip = createTestTrip("테스트 여행", "여행 설명", TripCategory.DOMESTIC, status);
        trip.addParticipant(TripParticipant.createTripParticipant(정수_ID, trip));
        tripRepository.save(trip);
        return trip;
    }

    private Trip createTestTrip(String title, String description, TripCategory category, TripStatus status) {
        Trip trip = TripFixture.createTestTrip(memberId, title, description, category);
        ReflectionTestUtils.setField(trip, "status", status);
        return tripRepository.save(trip);
    }

    private Trip createReadyTrip(UUID leaderId) {
        Trip trip = TripFixture.createReadyTrip(leaderId, "준비된 여행", "설명", TripCategory.DOMESTIC);
        return tripRepository.save(trip);
    }

    private Trip createProgressTrip(UUID leaderId) {
        Trip trip = TripFixture.createProgressTrip(leaderId, "진행중 여행", "설명", TripCategory.DOMESTIC);
        return tripRepository.save(trip);
    }

    @Test
    void 여행을_생성_한다() {
        TripCreateRequest request =
                new TripCreateRequest(
                        locationId,
                        "속초 여행 멤버 구함",
                        "https://k.kakaocdn.net/dn/image.jpg",
                        "속초 여행은 이렇게이렇게 갈겁니다~",
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5),
                        true,
                        "a".repeat(PASSWORD_MIN_LENGTH + 1),
                        4,
                        List.of("속초 여행", "MZ"),
                        TripCategory.DOMESTIC);
        TripCreateResponse response = tripService.createTrip(memberId, request);
        assertThat(response.id()).isNotNull();
        assertThat(response.destinationId()).isEqualTo(locationId);
        assertThat(response.hashTags()).contains("속초 여행", "MZ");
    }

    @Test
    void 여행_목록을_조회한다() {
        tripRepository.save(createTestTrip("속초 여행 맴버 구함", "속초 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC, TripStatus.RECRUITING));
        tripRepository.save(createTestTrip("대구 여행 멤버 구함", "대구 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC, TripStatus.RECRUITING));
        tripRepository.save(createTestTrip("부산 여행 멤버 구함", "부산 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC, TripStatus.RECRUITING));

        Page<TripResponse> trips = tripService.getTrips(PageRequest.of(0, 2));
        assertThat(trips.getTotalElements()).isEqualTo(3);
        assertThat(trips.getPageable().getOffset()).isEqualTo(0);
        assertThat(trips.getPageable().getPageSize()).isEqualTo(2);
        assertThat(trips.getContent().getFirst().hashTags()).contains("test", "Test 해시 코드");
    }


    @Test
    void 빈_여행_일정을_수정한다() {
        // given
        Trip trip = createTestTrip("테스트 여행", "여행 설명", TripCategory.DOMESTIC, TripStatus.RECRUITING);

        // then
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);
        PeriodUpdateRequest request = TripRequestFixture.createPeriod(
                start,
                end
        );
        PeriodUpdateResponse response = tripService.updatePeriod(memberId, trip.getId(), request);

        // when
        assertThat(response.start()).isEqualTo(start);
        assertThat(response.end()).isEqualTo(end);
        assertThat(response.itineraries().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 시작 전 ~ 이전 일정 시작 전'으로 변경할 수 있다")
    void updatePeriodIsBeforePrePeriodStart() {
        // given
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Trip trip = TripFixture.createTestTripWithPeriod(memberId, "강릉 여행 멤버 구함", "강릉 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC, period);
        tripRepository.save(trip);

        // when
        PeriodUpdateRequest request = TripRequestFixture.createPeriod(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );
        PeriodUpdateResponse response = tripService.updatePeriod(memberId, trip.getId(), request);

        // then
        assertThat(response.itineraries()).hasSize(3);
        assertThat(response.itineraries().stream()
                .map(PeriodUpdateResponse.ItineraryUpdateResponse::name).toList())
                .containsExactly("day 1", "day 2", "day 3");
        assertThat(response.itineraries().stream()
                .map(PeriodUpdateResponse.ItineraryUpdateResponse::date).toList())
                .containsExactly(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2),
                        LocalDate.now().plusDays(3)
                );
    }


    @Test
    @DisplayName("변경 일자가 '이전 일정 시작 전 ~ 이전 일정 종료 전'으로 변경할 수 있다")
    void updatePeriodBetweenPrePeriodStartBeforeAndPrePeriodEndBefore() {
        // given
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Trip trip = TripFixture.createTestTripWithPeriod(memberId, "강릉 여행 멤버 구함", "강릉 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC, period);
        tripRepository.save(trip);

        // when
        PeriodUpdateRequest request = TripRequestFixture.createPeriod(

                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(8)
        );
        PeriodUpdateResponse response = tripService.updatePeriod(memberId, trip.getId(), request);

        // then
        assertThat(response.itineraries()).hasSize(6);
        assertThat(response.itineraries().stream()
                .map(PeriodUpdateResponse.ItineraryUpdateResponse::name).toList())
                .containsExactly("day 1", "day 2", "day 3", "day 4", "day 5", "day 6");
        assertThat(response.itineraries().stream()
                .map(PeriodUpdateResponse.ItineraryUpdateResponse::date).toList())
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
        // given
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));
        Trip trip = TripFixture.createTestTripWithPeriod(memberId, "강릉 여행 멤버 구함", "강릉 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC, period);
        tripRepository.save(trip);

        // when
        PeriodUpdateRequest request = TripRequestFixture.createPeriod(
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(8)
        );
        PeriodUpdateResponse response = tripService.updatePeriod(memberId, trip.getId(), request);

        // then
        assertThat(response.itineraries()).hasSize(6);
        assertThat(response.itineraries().stream()
                .map(PeriodUpdateResponse.ItineraryUpdateResponse::name).toList())
                .containsExactly("day 1", "day 2", "day 3", "day 4", "day 5", "day 6");
        assertThat(response.itineraries().stream()
                .map(PeriodUpdateResponse.ItineraryUpdateResponse::date).toList())
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
        // given
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Trip trip = TripFixture.createTestTripWithPeriod(memberId, "강릉 여행 멤버 구함", "강릉 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC, period);
        tripRepository.save(trip);


        // when
        PeriodUpdateRequest request = TripRequestFixture.createPeriod(

                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(9)
        );
        PeriodUpdateResponse response = tripService.updatePeriod(memberId, trip.getId(), request);

        // then
        assertThat(response.itineraries()).hasSize(3);
        assertThat(response.itineraries().stream()
                .map(PeriodUpdateResponse.ItineraryUpdateResponse::name).toList())
                .containsExactly("day 1", "day 2", "day 3");
        assertThat(response.itineraries().stream()
                .map(PeriodUpdateResponse.ItineraryUpdateResponse::date).toList())
                .containsExactly(
                        LocalDate.now().plusDays(7),
                        LocalDate.now().plusDays(8),
                        LocalDate.now().plusDays(9)
                );
    }

    @Test
    @DisplayName("변경 일자가 '이전 일정 시작 후 ~ 이전 일정 종료 후'으로 변경할 수 있다")
    void updatePeriodBetweenPrePeriodStartAfterAndPrePeriodEndAfter() {
        // given
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Trip trip = TripFixture.createTestTripWithPeriod(memberId, "강릉 여행 멤버 구함", "강릉 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC, period);
        tripRepository.save(trip);


        // when
        PeriodUpdateRequest request = TripRequestFixture.createPeriod(
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(12)
        );
        PeriodUpdateResponse response = tripService.updatePeriod(memberId, trip.getId(), request);

        // then
        assertThat(response.itineraries()).hasSize(6);
        assertThat(response.itineraries().stream()
                .map(PeriodUpdateResponse.ItineraryUpdateResponse::name).toList())
                .containsExactly("day 1", "day 2", "day 3", "day 4", "day 5", "day 6");
        assertThat(response.itineraries().stream()
                .map(PeriodUpdateResponse.ItineraryUpdateResponse::date).toList())
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

        // given
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Trip trip = TripFixture.createTestTripWithPeriod(memberId, "강릉 여행 멤버 구함", "강릉 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC, period);
        tripRepository.save(trip);


        // when
        PeriodUpdateRequest request = TripRequestFixture.createPeriod(

                LocalDate.now().plusDays(11),
                LocalDate.now().plusDays(14)
        );
        PeriodUpdateResponse response = tripService.updatePeriod(memberId, trip.getId(), request);

        // then
        assertThat(response.itineraries()).hasSize(4);
        assertThat(response.itineraries().stream()
                .map(PeriodUpdateResponse.ItineraryUpdateResponse::name).toList())
                .containsExactly("day 1", "day 2", "day 3", "day 4");
        assertThat(response.itineraries().stream()
                .map(PeriodUpdateResponse.ItineraryUpdateResponse::date).toList())
                .containsExactly(
                        LocalDate.now().plusDays(11),
                        LocalDate.now().plusDays(12),
                        LocalDate.now().plusDays(13),
                        LocalDate.now().plusDays(14)
                );
    }

    @Test
    @DisplayName("멤버가 성공적으로 여행을 나간다")
    void leaveTrip_success_forMember() {
        // given
        Trip trip = createReadyTrip(memberId);
        trip.addParticipant(TripParticipant.createTripParticipant(newMemberId, trip));
        tripRepository.save(trip);

        // when
        tripService.leaveTrip(trip.getId(), newMemberId);

        // then
        Trip updatedTrip = tripRepository.findById(trip.getId()).get();
        boolean isParticipantPresent = updatedTrip.getTripParticipants().findParticipantById(newMemberId).isPresent();
        assertThat(isParticipantPresent).isFalse();
    }

    @Test
    @DisplayName("리더는 위임 없이 여행을 나갈 수 없다")
    void leaveTrip_fail_forLeader() {
        // given
        Trip trip = createReadyTrip(memberId);

        // when & then
        assertThrows(LeaderCannotLeaveException.class, () -> {
            tripService.leaveTrip(trip.getId(), memberId);
        });
    }

    @Test
    @DisplayName("여행이 '여행 전' 상태가 아니면 나갈 수 없다")
    void leaveTrip_fail_whenTripNotReady() {
        // given
        Trip trip = createProgressTrip(memberId);
        trip.addParticipant(TripParticipant.createTripParticipant(newMemberId, trip));
        tripRepository.save(trip);

        // when & then
        assertThrows(TripNotReadyException.class, () -> {
            tripService.leaveTrip(trip.getId(), newMemberId);
        });
    }

    @Test
    @DisplayName("리더가 성공적으로 다른 멤버에게 리더를 위임한다")
    void delegateLeader_success() {
        // given
        Trip trip = createReadyTrip(memberId);
        trip.addParticipant(TripParticipant.createTripParticipant(newMemberId, trip));
        tripRepository.save(trip);
        DelegateLeaderRequest request = new DelegateLeaderRequest(memberId, newMemberId);

        // when
        tripService.delegateLeader(trip.getId(), request);

        // then
        Trip updatedTrip = tripRepository.findById(trip.getId()).get();
        assertTrue(updatedTrip.getTripParticipants().findParticipantById(newMemberId).get().isLeader());
        assertThat(updatedTrip.getTripParticipants().findParticipantById(memberId).get().isLeader()).isFalse();
    }

    @Test
    @DisplayName("리더가 아닌 멤버는 리더를 위임할 수 없다")
    void delegateLeader_fail_notLeader() {
        // given
        Trip trip = createReadyTrip(memberId);
        trip.addParticipant(TripParticipant.createTripParticipant(newMemberId, trip));
        tripRepository.save(trip);
        DelegateLeaderRequest request = new DelegateLeaderRequest(newMemberId, memberId);

        // when & then
        assertThrows(MemberIsNotLeaderException.class, () -> {
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
        assertThrows(InvalidValueException.class, () -> {
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
        assertThrows(NotParticipantException.class, () -> {
            tripService.delegateLeader(trip.getId(), request);
        });
    }

    @Test
    void 나의_여행_목록_리스트를_조회한다() {
        // given
        Trip 정수_joinedTrip1 = createTestTripWithParticipants(TripStatus.RECRUITING);
        Trip 정수_joinedTrip2 = createTestTripWithParticipants(TripStatus.RECRUITMENT_CLOSED);
        Trip 정수_joinedTrip3 = createTestTripWithParticipants(TripStatus.IN_PROGRESS);
        Trip 정수_joinedTrip4 = createTestTripWithParticipants(TripStatus.COMPLETED);

        // when
        Page<MyTripResponse> myTrips = tripService.getMyTrips(정수_ID, null, PageRequest.of(0, 10));
        List<UUID> tripIds = myTrips.getContent()
                .stream()
                .map(MyTripResponse::id)
                .toList();

        // then
        assertThat(myTrips).isNotNull();
        assertThat(myTrips.getTotalElements()).isEqualTo(3);
        assertThat(myTrips.getContent()).hasSize(3);

        assertThat(tripIds)
                .containsExactlyInAnyOrder(
                        정수_joinedTrip1.getId(),
                        정수_joinedTrip2.getId(),
                        정수_joinedTrip3.getId()
                );
    }

    @Test
    void 나의_종료된_여행이담긴_보관함을_조회한다() {
        // given
        Trip 정수_joinedTrip1 = createTestTripWithParticipants(TripStatus.RECRUITING);
        Trip 정수_joinedTrip2 = createTestTripWithParticipants(TripStatus.IN_PROGRESS);
        Trip 정수_joinedTrip3 = createTestTripWithParticipants(TripStatus.COMPLETED);
        Trip 정수_joinedTrip4 = createTestTripWithParticipants(TripStatus.COMPLETED);

        // when
        Page<MyTripResponse> myTrips = tripService.getMyTrips(정수_ID, TripStatus.COMPLETED, PageRequest.of(0, 10));
        List<UUID> tripIds = myTrips.getContent()
                .stream()
                .map(MyTripResponse::id)
                .toList();

        // then
        assertThat(myTrips).isNotNull();
        assertThat(myTrips.getTotalElements()).isEqualTo(2);
        assertThat(myTrips.getContent()).hasSize(2);

        assertThat(tripIds)
                .containsExactlyInAnyOrder(
                        정수_joinedTrip3.getId(),
                        정수_joinedTrip4.getId()
                );
    }

    @Test
    void 사용자는_여행방을_나갈_수_있다() {
        // given
        Trip trip = createReadyTrip(memberId);
        trip.addParticipant(TripParticipant.createTripParticipant(newMemberId, trip));
        tripRepository.save(trip);

        // when
        tripService.leaveTrip(trip.getId(), newMemberId);

        // then
        assertThat(trip.getTripParticipants().getValues().size()).isOne();
        assertThat(trip.getTripParticipants().getValues().get(0).getMemberId()).isNotEqualTo(newMemberId);
    }

    @Test
    void 리더는_참여자들을_추방할_수_있다() {
        // given
        Trip newTrip = createTestTripWithParticipants(TripStatus.RECRUITING);

        // then
        tripService.banMembers(memberId, newTrip.getId(), List.of(정수_ID));

        Trip trip = tripRepository.findById(newTrip.getId()).orElseThrow();
        TripParticipant banParticipant = trip.getTripParticipants().getValues().stream()
                .filter(participant -> participant.getMemberId().equals(정수_ID))
                .findFirst()
                .orElseThrow();

        // when
        assertThat(banParticipant.getStatus()).isEqualTo(ParticipantStatus.EXPELLED);
    }

    @Test
    @DisplayName("여행 리더는 최대 참여 인원을 변경할 수 있다.")
    void leader_can_update_maxParticipants() {
        // given
        Trip trip = TripFixture.createTestTripWithMaxParticipants(memberId, "TEST", "TEST", TripCategory.DOMESTIC, 3);
        tripRepository.save(trip);

        // when
        int newMaxParticipants = 5;
        trip.getTripParticipants().updateMaxParticipants(newMaxParticipants, memberId);
        tripRepository.save(trip);

        // then
        Trip updatedTrip = tripRepository.findById(trip.getId()).get();
        assertThat(updatedTrip.getTripParticipants().getMaxParticipants()).isEqualTo(newMaxParticipants);
    }

    @Test
    @DisplayName("리더가 아닌 멤버는 최대 참여 인원을 변경할 수 없다.")
    void nonLeader_cannot_update_maxParticipants() {
        // given
        UUID nonLeaderId = UUID.randomUUID();
        Trip trip = TripFixture.createTestTripWithMaxParticipants(memberId, "TEST", "TEST", TripCategory.DOMESTIC, 3);
        trip.addParticipant(TripParticipant.createTripParticipant(nonLeaderId, trip));
        tripRepository.save(trip);


        // when & then
        assertThrows(MemberIsNotLeaderException.class, () -> {
            trip.getTripParticipants().updateMaxParticipants(5, nonLeaderId);
        });
    }

    @Test
    @DisplayName("최대 참여 인원을 현재 참여 인원보다 적게 변경할 수 없다.")
    void cannot_update_maxParticipants_lessThan_currentParticipants() {
        // given
        Trip trip = TripFixture.createTestTrip(memberId, "인원 축소 테스트", "설명", TripCategory.DOMESTIC);
        trip.addParticipant(TripParticipant.createTripParticipant(UUID.randomUUID(), trip));
        trip.addParticipant(TripParticipant.createTripParticipant(UUID.randomUUID(), trip));
        tripRepository.save(trip);

        // when & then
        assertThrows(InvalidValueException.class, () -> {
            trip.getTripParticipants().updateMaxParticipants(2, memberId);
        });
    }

    @Test
    @DisplayName("isLeader 메서드가 정확하게 리더와 멤버를 구분하는지 확인한다.")
    void isLeader_check_works_correctly() {
        // given
        UUID nonLeaderId = UUID.randomUUID();
        Trip trip = TripFixture.createTestTrip(memberId, "isLeader 테스트", "설명", TripCategory.DOMESTIC);
        trip.addParticipant(TripParticipant.createTripParticipant(nonLeaderId, trip));
        tripRepository.save(trip);

        // when
        Trip savedTrip = tripRepository.findById(trip.getId()).get();
        boolean isLeaderResult = savedTrip.getTripParticipants().requireLeader(memberId);
        boolean isNotLeaderResult = savedTrip.getTripParticipants().requireLeader(nonLeaderId);


        // then
        assertTrue(isLeaderResult);
        assertThrows(InvalidValueException.class, () -> {
            savedTrip.getTripParticipants().requireLeader(UUID.randomUUID());
        });
    }

    @Test
    void 여행확정요청_생성_성공() {
        //given
        Trip trip = createTestTripWithParticipants(TripStatus.RECRUITING);
        trip.changeStatusToRecruitmentClosed();
        TripConfirmationDemandRequest request = new TripConfirmationDemandRequest(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));

        //when
        tripService.demandTripConfirmation(memberId, trip.getId(), request);
        TripConfirmationDemand demand = tripConfirmationDemandRepository.findAll().get(0);

        //then
        assertThat(demand).isNotNull();
        assertThat(demand.getTrip().getId()).isEqualTo(trip.getId());
    }

    @Test
    void 여행확정_재요청_성공() {
        //given
        Trip trip = createTestTripWithParticipants(TripStatus.RECRUITING);
        trip.changeStatusToRecruitmentClosed();
        TripConfirmationDemandRequest request = new TripConfirmationDemandRequest(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        TripConfirmationDemand demand = TripConfirmationDemand.create(memberId, trip, request.startDate(), request.endDate());
        demand.addTripMember(memberId);
        tripConfirmationDemandRepository.save(demand);
        TripConfirmationDemandRequest newRequest = new TripConfirmationDemandRequest(LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));

        //when
        tripService.demandAgainTripConfirmation(memberId, trip.getId(), demand.getId(), newRequest);
        TripConfirmationDemand updated = tripConfirmationDemandRepository.findById(demand.getId()).orElseThrow();

        //then
        assertThat(updated.getConfirmStartDate()).isEqualTo(newRequest.startDate());
    }

    @Test
    void 여행확정요청_수락_성공() {
        //given
        Trip trip = createTestTripWithParticipants(TripStatus.RECRUITING);
        trip.changeStatusToRecruitmentClosed();
        TripConfirmationDemand demand = TripConfirmationDemand.create(memberId, trip, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        demand.addTripMember(memberId);
        tripConfirmationDemandRepository.save(demand);

        //when
        ConfirmationDemandAcceptResponse response = tripService.acceptConfirmationDemand(정수_ID, trip.getId(), demand.getId());

        //then
        assertThat(response).isNotNull();
        assertThat(response.tripId()).isEqualTo(trip.getId());
    }


    @Test
    void 여행확정요청_거절_성공() {
        //given
        Trip trip = createTestTripWithParticipants(TripStatus.RECRUITING);
        trip.changeStatusToRecruitmentClosed();
        TripConfirmationDemand demand = TripConfirmationDemand.create(memberId, trip, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        demand.addTripMember(memberId);
        tripConfirmationDemandRepository.save(demand);

        //when
        tripService.rejectConfirmationDemand(정수_ID, trip.getId(), demand.getId());
        TripConfirmationDemand rejected = tripConfirmationDemandRepository.findById(demand.getId()).orElseThrow();

        //then
        assertThat(rejected.getReplies().getValues().stream().anyMatch(TripConfirmationReply::isAccepted)).isFalse();
    }

    @Test
    void 사용자는_여행_상세를_조회할_수_있다() {
        //given
        Trip trip = createTestTripWithParticipants(TripStatus.RECRUITING);

        //when
        TripDetailResponse tripDetail = tripService.getTripDetail(memberId, trip.getId());
        TripDetailResponse expectedTripDetail =
                new TripDetailResponse(
                        trip.getId(),
                        true,
                        true,
                        "테스트 여행",
                        trip.getCreatedAt(),
                        2,
                        4,
                        TripStatus.RECRUITING,
                        TripStatus.RECRUITING.getViewName(),
                        "",
                        TEST_IMAGE_URL,
                        "여행 설명",
                        List.of("test", "Test 해시 코드"),
                        List.of(
                                new TripParticipantResponse(UUID.randomUUID(), memberId, "안녕하세요 테스트 입니다", "홍길동", TEST_IMAGE_URL, ParticipantRole.LEADER),
                                new TripParticipantResponse(UUID.randomUUID(), 정수_ID, "안녕하세요 박정수 입니다", "홍길동", TEST_IMAGE_URL, ParticipantRole.PARTICIPANT)
                        )
                );


        //then
        assertThat(tripDetail).usingRecursiveComparison()
                .ignoringFields("participants.participantId")
                .isEqualTo(expectedTripDetail);
    }
}
