package com.retrip.trip.application.in;

import com.retrip.trip.application.in.base.BaseTripServiceTest;
import com.retrip.trip.application.in.request.*;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.response.TripDetailResponse.TripParticipantResponse;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.entity.demand.Demand;
import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.exception.LeaderCannotLeaveException;
import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.NotParticipantException;
import com.retrip.trip.domain.exception.TripNotReadyException;
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

import static com.retrip.trip.domain.fixture.TripFixture.정수_ID;
import static com.retrip.trip.domain.vo.TripPassword.PASSWORD_MIN_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TripServiceTest extends BaseTripServiceTest {

    private static final String TEST_IMAGE_URL = "https://test-image.com/default.jpg";

    private Trip createTestTripWithParticipantsAndHashTags(TripStatus status, List<HashTagInfo> hashTags) {
        Trip trip = createTestTripWithHashTags("테스트 여행", "여행 설명", TripCategory.DOMESTIC, status, hashTags);
        trip.addParticipant(TripParticipant.createTripParticipant(정수_ID, trip));
        tripRepository.save(trip);
        return trip;
    }

    private Trip createTestTripWithHashTags(String title, String description, TripCategory category, TripStatus status, List<HashTagInfo> hashTags) {
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
        Trip trip = Trip.create(
                memberId,
                List.of(UUID.randomUUID()),
                new TripTitle(title),
                "https://test-image.com/default.jpg",
                new TripDescription(description),
                period,
                true,
                4,
                hashTags,
                category,
                TripStatus.RECRUITING
        );
        ReflectionTestUtils.setField(trip, "status", status);
        return tripRepository.save(trip);
    }

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
        // given
        List<UUID> locationIds = List.of(locationId, UUID.randomUUID());
        List<TripCreateRequest.HashTagInput> hashTags = List.of(
                new TripCreateRequest.HashTagInput("남자", 1),
                new TripCreateRequest.HashTagInput("20대", 2)
        );

        TripCreateRequest request = new TripCreateRequest(
                        locationIds,
                        "속초 여행 멤버 구함",
                        "https://k.kakaocdn.net/dn/image.jpg",
                        "속초 여행은 이렇게이렇게 갈겁니다~",
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5),
                        true,
                        "a".repeat(PASSWORD_MIN_LENGTH + 1),
                        4,
                        hashTags,
                        TripCategory.DOMESTIC);

        // when
        TripCreateResponse response = tripService.createTripWithItineraries(memberId, request);

        // then
        assertThat(response.id()).isNotNull();
        assertThat(response.destinationIds()).hasSize(2);
        assertThat(response.destinationIds()).contains(locationId);
        assertThat(response.hashTags()).hasSize(2);
        assertThat(response.hashTags().get(0).tag()).isEqualTo("남자");
        assertThat(response.hashTags().get(0).order()).isEqualTo(1);
        assertThat(response.hashTags().get(1).tag()).isEqualTo("20대");
        assertThat(response.hashTags().get(1).order()).isEqualTo(2);
    }

    @Test
    void 여행_생성시_여행지를_다수_등록할_수_있다() {
        UUID destinationId1 = UUID.randomUUID();
        UUID destinationId2 = UUID.randomUUID();
        UUID destinationId3 = UUID.randomUUID();

        TripCreateRequest request = new TripCreateRequest(
                List.of(destinationId1, destinationId2, destinationId3),
                "유럽 여행",
                null,
                "유럽 3개국 여행",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10),
                true,
                null,
                4,
                List.of(new TripCreateRequest.HashTagInput("남자", 1)),
                TripCategory.OVERSEAS);

        TripCreateResponse response = tripService.createTripWithItineraries(memberId, request);

        assertThat(response.destinationIds()).hasSize(3);
        assertThat(response.destinationIds()).containsExactlyInAnyOrder(destinationId1, destinationId2, destinationId3);
    }

    @Test
    void 여행_생성시_해시태그_순서가_보장된다() {
        List<TripCreateRequest.HashTagInput> hashTags = List.of(
                new TripCreateRequest.HashTagInput("세번째", 3),
                new TripCreateRequest.HashTagInput("첫번째", 1),
                new TripCreateRequest.HashTagInput("두번째", 2)
        );

        TripCreateRequest request = new TripCreateRequest(
                List.of(UUID.randomUUID()),
                "순서 테스트",
                null,
                "해시태그 순서 테스트",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                true,
                null,
                4,
                hashTags,
                TripCategory.DOMESTIC);

        TripCreateResponse response = tripService.createTripWithItineraries(memberId, request);

        assertThat(response.hashTags().get(0).tag()).isEqualTo("첫번째");
        assertThat(response.hashTags().get(1).tag()).isEqualTo("두번째");
        assertThat(response.hashTags().get(2).tag()).isEqualTo("세번째");
    }

    @Test
    void 여행_생성시_소개글이_250자를_초과하면_실패한다() {
        String longDescription = "a".repeat(251);

        TripCreateRequest request = new TripCreateRequest(
                List.of(UUID.randomUUID()),
                "테스트 여행",
                null,
                longDescription,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                true,
                null,
                4,
                List.of(new TripCreateRequest.HashTagInput("태그", 1)),
                TripCategory.DOMESTIC);

        assertThrows(IllegalArgumentException.class, () -> {
            tripService.createTripWithItineraries(memberId, request);
        });
    }

    @Test
    void 여행_생성시_해시태그가_10자를_초과하면_실패한다() {
        List<TripCreateRequest.HashTagInput> hashTags = List.of(
                new TripCreateRequest.HashTagInput("a".repeat(11), 1)
        );

        TripCreateRequest request = new TripCreateRequest(
                List.of(UUID.randomUUID()),
                "테스트 여행",
                null,
                "설명",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                true,
                null,
                4,
                hashTags,
                TripCategory.DOMESTIC);

        assertThrows(BusinessException.class, () -> {
            tripService.createTripWithItineraries(memberId, request);
        });
    }

    @Test
    void 여행_목록을_조회한다() {
        tripRepository.save(createTestTrip("속초 여행 맴버 구함", "속초 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC, TripStatus.RECRUITING));
        tripRepository.save(createTestTrip("대구 여행 멤버 구함", "대구 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC, TripStatus.RECRUITING));
        tripRepository.save(createTestTrip("부산 여행 멤버 구함", "부산 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC, TripStatus.RECRUITING));

        Page<TripResponse> trips = tripService.getTrips(null, null, null, PageRequest.of(0, 2));
        assertThat(trips.getTotalElements()).isEqualTo(3);
        assertThat(trips.getPageable().getOffset()).isEqualTo(0);
        assertThat(trips.getPageable().getPageSize()).isEqualTo(2);

        List<TripResponse.HashTagResponse> hashTags = trips.getContent().getFirst().hashTags();
        assertThat(hashTags).hasSize(2);
        assertThat(hashTags.get(0).tag()).isEqualTo("남자");
        assertThat(hashTags.get(0).order()).isEqualTo(1);
        assertThat(hashTags.get(1).tag()).isEqualTo("20대");
        assertThat(hashTags.get(1).order()).isEqualTo(2);
    }

    @Test
    void 여행_목록을_상태_필터로_조회한다() {
        // given
        tripRepository.save(createTestTrip("모집중 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING));
        tripRepository.save(createTestTrip("진행중 여행", "설명", TripCategory.DOMESTIC, TripStatus.IN_PROGRESS));
        tripRepository.save(createTestTrip("완료 여행", "설명", TripCategory.DOMESTIC, TripStatus.COMPLETED));

        // when
        Page<TripResponse> trips = tripService.getTrips(List.of(TripStatus.RECRUITING), null, null, PageRequest.of(0, 10));

        // then
        assertThat(trips.getTotalElements()).isEqualTo(1);
        assertThat(trips.getContent().getFirst().title()).isEqualTo("모집중 여행");
    }

    @Test
    void 여행_목록을_성별_필터로_조회한다() {
        // given
        tripRepository.save(createTestTripWithHashTags("남자 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("20대", 2))));
        tripRepository.save(createTestTripWithHashTags("여자 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING,
                List.of(new HashTagInfo("여자", 1), new HashTagInfo("30대", 2))));
        tripRepository.save(createTestTripWithHashTags("혼성 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING,
                List.of(new HashTagInfo("혼성", 1), new HashTagInfo("20대", 2))));

        // when
        Page<TripResponse> trips = tripService.getTrips(null, List.of("남자"), null, PageRequest.of(0, 10));

        // then
        assertThat(trips.getTotalElements()).isEqualTo(1);
        assertThat(trips.getContent().getFirst().title()).isEqualTo("남자 여행");
    }

    @Test
    void 여행_목록을_나이_필터로_조회한다() {
        // given
        tripRepository.save(createTestTripWithHashTags("20대 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("20대", 2))));
        tripRepository.save(createTestTripWithHashTags("30대 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING,
                List.of(new HashTagInfo("여자", 1), new HashTagInfo("30대", 2))));

        // when
        Page<TripResponse> trips = tripService.getTrips(null, null, List.of("20대"), PageRequest.of(0, 10));

        // then
        assertThat(trips.getTotalElements()).isEqualTo(1);
        assertThat(trips.getContent().getFirst().title()).isEqualTo("20대 여행");
    }

    @Test
    void 여행_목록을_복수_필터로_조회한다() {
        // given
        tripRepository.save(createTestTripWithHashTags("남자 20대", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("20대", 2))));
        tripRepository.save(createTestTripWithHashTags("여자 30대", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING,
                List.of(new HashTagInfo("여자", 1), new HashTagInfo("30대", 2))));
        tripRepository.save(createTestTripWithHashTags("혼성 40대", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING,
                List.of(new HashTagInfo("혼성", 1), new HashTagInfo("40대", 2))));

        // when
        Page<TripResponse> trips = tripService.getTrips(null, List.of("남자", "여자"), List.of("30대"), PageRequest.of(0, 10));

        // then
        assertThat(trips.getTotalElements()).isEqualTo(2);
        List<String> titles = trips.getContent().stream().map(TripResponse::title).toList();
        assertThat(titles).containsExactlyInAnyOrder("남자 20대", "여자 30대");
    }

    @Test
    void 여행_목록을_상태와_해시태그_필터를_함께_조회한다() {
        // given
        tripRepository.save(createTestTripWithHashTags("모집중 남자", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("20대", 2))));
        tripRepository.save(createTestTripWithHashTags("완료 남자", "설명", TripCategory.DOMESTIC, TripStatus.COMPLETED,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("30대", 2))));
        tripRepository.save(createTestTripWithHashTags("모집중 여자", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING,
                List.of(new HashTagInfo("여자", 1), new HashTagInfo("20대", 2))));

        // when
        Page<TripResponse> trips = tripService.getTrips(List.of(TripStatus.RECRUITING), List.of("남자"), null, PageRequest.of(0, 10));

        // then
        assertThat(trips.getTotalElements()).isEqualTo(1);
        assertThat(trips.getContent().getFirst().title()).isEqualTo("모집중 남자");
    }

    @Test
    void 여행_목록_필터_조건에_맞는_여행이_없으면_빈_목록을_반환한다() {
        // given
        tripRepository.save(createTestTripWithHashTags("남자 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("20대", 2))));

        // when
        Page<TripResponse> trips = tripService.getTrips(null, List.of("여자"), List.of("60대이상"), PageRequest.of(0, 10));

        // then
        assertThat(trips.getTotalElements()).isEqualTo(0);
        assertThat(trips.getContent()).isEmpty();
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
    @DisplayName("여행이 시작된 이후에는 나갈 수 없다")
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
        DelegateLeaderRequest request = new DelegateLeaderRequest(newMemberId);

        // when
        tripService.delegateLeader(trip.getId(), memberId, request);

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
        DelegateLeaderRequest request = new DelegateLeaderRequest(memberId);

        // when & then
        assertThrows(MemberIsNotLeaderException.class, () -> {
            tripService.delegateLeader(trip.getId(), newMemberId, request);
        });
    }

    @Test
    @DisplayName("리더는 자기 자신에게 리더를 위임할 수 없다")
    void delegateLeader_fail_toSelf() {
        // given
        Trip trip = createReadyTrip(memberId);
        DelegateLeaderRequest request = new DelegateLeaderRequest(memberId);

        // when & then
        assertThrows(BusinessException.class, () -> {
            tripService.delegateLeader(trip.getId(), memberId, request);
        });
    }

    @Test
    @DisplayName("참여자가 아닌 사람에게 리더를 위임할 수 없다")
    void delegateLeader_fail_toNonParticipant() {
        // given
        Trip trip = createReadyTrip(memberId);
        UUID nonParticipantId = UUID.randomUUID();
        DelegateLeaderRequest request = new DelegateLeaderRequest(nonParticipantId);

        // when & then
        assertThrows(NotParticipantException.class, () -> {
            tripService.delegateLeader(trip.getId(), memberId, request);
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
        Page<MyTripResponse> myTrips = tripService.getMyTrips(정수_ID, null, null, null, PageRequest.of(0, 10));
        List<UUID> tripIds = myTrips.getContent()
                .stream()
                .map(MyTripResponse::id)
                .toList();

        // then
        assertThat(myTrips).isNotNull();
        assertThat(myTrips.getTotalElements()).isEqualTo(4);
        assertThat(myTrips.getContent()).hasSize(4);

        assertThat(tripIds)
                .containsExactlyInAnyOrder(
                        정수_joinedTrip1.getId(),
                        정수_joinedTrip2.getId(),
                        정수_joinedTrip3.getId(),
                        정수_joinedTrip4.getId()
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
        Page<MyTripResponse> myTrips = tripService.getMyTrips(정수_ID, List.of(TripStatus.COMPLETED), null, null, PageRequest.of(0, 10));
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
    void 나의_여행_목록을_성별_필터로_조회한다() {
        // given
        Trip 남자여행 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("20대", 2))
        );
        Trip 여자여행 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("여자", 1), new HashTagInfo("30대", 2))
        );
        Trip 혼성여행 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("혼성", 1), new HashTagInfo("20대", 2))
        );

        // when
        Page<MyTripResponse> myTrips = tripService.getMyTrips(
                정수_ID, null, List.of("남자"), null, PageRequest.of(0, 10));

        // then
        assertThat(myTrips.getContent()).hasSize(1);
        assertThat(myTrips.getContent().get(0).id()).isEqualTo(남자여행.getId());
    }

    @Test
    void 나의_여행_목록을_나이_필터로_조회한다() {
        // given
        Trip 이십대여행 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("20대", 2))
        );
        Trip 삼십대여행 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("여자", 1), new HashTagInfo("30대", 2))
        );

        // when
        Page<MyTripResponse> myTrips = tripService.getMyTrips(
                정수_ID, null, null, List.of("20대"), PageRequest.of(0, 10));

        // then
        assertThat(myTrips.getContent()).hasSize(1);
        assertThat(myTrips.getContent().get(0).id()).isEqualTo(이십대여행.getId());
    }

    @Test
    void 나의_여행_목록을_성별과_나이_필터로_조회한다() {
        // given
        Trip 남자_20대 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("20대", 2))
        );
        Trip 여자_30대 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("여자", 1), new HashTagInfo("30대", 2))
        );
        Trip 남자_30대 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("30대", 2))
        );

        // when
        Page<MyTripResponse> myTrips = tripService.getMyTrips(
                정수_ID, null, List.of("남자"), List.of("20대"), PageRequest.of(0, 10));

        // then
        List<UUID> tripIds = myTrips.getContent().stream()
                .map(MyTripResponse::id)
                .toList();

        assertThat(tripIds).containsExactlyInAnyOrder(
                남자_20대.getId(),
                남자_30대.getId()
        );
    }

    @Test
    void 나의_여행_목록을_복수_성별_필터로_조회한다() {
        // given
        Trip 남자여행 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("20대", 2))
        );
        Trip 여자여행 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("여자", 1), new HashTagInfo("30대", 2))
        );
        Trip 혼성여행 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("혼성", 1), new HashTagInfo("20대", 2))
        );

        // when
        Page<MyTripResponse> myTrips = tripService.getMyTrips(
                정수_ID, null, List.of("남자", "혼성"), null, PageRequest.of(0, 10));

        // then
        List<UUID> tripIds = myTrips.getContent().stream()
                .map(MyTripResponse::id)
                .toList();

        assertThat(tripIds).containsExactlyInAnyOrder(
                남자여행.getId(),
                혼성여행.getId()
        );
    }

    @Test
    void 나의_여행_목록을_복수_나이_필터로_조회한다() {
        // given
        Trip 이십대여행 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("20대", 2))
        );
        Trip 삼십대여행 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("여자", 1), new HashTagInfo("30대", 2))
        );
        Trip 사십대여행 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("혼성", 1), new HashTagInfo("40대", 2))
        );

        // when
        Page<MyTripResponse> myTrips = tripService.getMyTrips(
                정수_ID, null, null, List.of("20대", "30대"), PageRequest.of(0, 10));

        // then
        List<UUID> tripIds = myTrips.getContent().stream()
                .map(MyTripResponse::id)
                .toList();

        assertThat(tripIds).containsExactlyInAnyOrder(
                이십대여행.getId(),
                삼십대여행.getId()
        );
    }

    @Test
    void 나의_여행_목록을_상태와_성별_필터를_함께_조회한다() {
        // given
        Trip 모집중_남자 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("20대", 2))
        );
        Trip 완료_남자 = createTestTripWithParticipantsAndHashTags(
                TripStatus.COMPLETED,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("30대", 2))
        );
        Trip 모집중_여자 = createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("여자", 1), new HashTagInfo("20대", 2))
        );

        // when
        Page<MyTripResponse> myTrips = tripService.getMyTrips(
                정수_ID, List.of(TripStatus.RECRUITING), List.of("남자"), null, PageRequest.of(0, 10));

        // then - COMPLETED는 기본 조회에서 제외되므로 모집중_남자만
        assertThat(myTrips.getContent()).hasSize(1);
        assertThat(myTrips.getContent().get(0).id()).isEqualTo(모집중_남자.getId());
    }

    @Test
    void 필터_조건에_맞는_여행이_없으면_빈_목록을_반환한다() {
        // given
        createTestTripWithParticipantsAndHashTags(
                TripStatus.RECRUITING,
                List.of(new HashTagInfo("남자", 1), new HashTagInfo("20대", 2))
        );

        // when
        Page<MyTripResponse> myTrips = tripService.getMyTrips(
                정수_ID, null, List.of("여자"), List.of("60대이상"), PageRequest.of(0, 10));

        // then
        assertThat(myTrips.getContent()).isEmpty();
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

//    @Disabled("TripConfirmationUseCase 미구현 - TripService에서 주석 처리된 상태")
//    @Test
//    void 여행확정요청_생성_성공() {
//        // TODO: TripConfirmationUseCase 구현 후 활성화
//    }
//
//    @Disabled("TripConfirmationUseCase 미구현 - TripService에서 주석 처리된 상태")
//    @Test
//    void 여행확정_재요청_성공() {
//        // TODO: TripConfirmationUseCase 구현 후 활성화
//    }
//
//    @Disabled("TripConfirmationUseCase 미구현 - TripService에서 주석 처리된 상태")
//    @Test
//    void 여행확정요청_수락_성공() {
//        // TODO: TripConfirmationUseCase 구현 후 활성화
//    }
//
//    @Disabled("TripConfirmationUseCase 미구현 - TripService에서 주석 처리된 상태")
//    @Test
//    void 여행확정요청_거절_성공() {
//        // TODO: TripConfirmationUseCase 구현 후 활성화
//    }

    @Test
    void 사용자는_여행_상세를_조회할_수_있다() {
        //given
        Trip trip = createTestTripWithParticipants(TripStatus.RECRUITING);

        //when
        TripDetailResponse tripDetail = tripService.getTripDetail(memberId, trip.getId());

        //then
        assertThat(tripDetail.id()).isEqualTo(trip.getId());
        assertThat(tripDetail.isLeader()).isTrue();
        assertThat(tripDetail.isParticipant()).isTrue();
        assertThat(tripDetail.title()).isEqualTo("테스트 여행");
        assertThat(tripDetail.participantCount()).isEqualTo(2);
        assertThat(tripDetail.maxParticipantCount()).isEqualTo(4);
        assertThat(tripDetail.tripStatus()).isEqualTo(TripStatus.RECRUITING);
        assertThat(tripDetail.hashTags()).containsExactly(
                new TripDetailResponse.HashTagResponse("남자", 1),
                new TripDetailResponse.HashTagResponse("20대", 2)
        );
        assertThat(tripDetail.participants()).hasSize(2);
        assertThat(tripDetail.participants().stream().map(TripParticipantResponse::memberId).toList())
                .containsExactlyInAnyOrder(memberId, 정수_ID);
        assertThat(tripDetail.participants().stream().map(TripParticipantResponse::role).toList())
                .containsExactlyInAnyOrder(ParticipantRole.LEADER, ParticipantRole.PARTICIPANT);
        // Auth 스텁이 빈 리스트 반환하므로 회원 정보 필드는 null
        assertThat(tripDetail.participants()).allMatch(p -> p.nickName() == null && p.introduction() == null && p.imageUrl() == null);
    }

    @Test
    void 여행_상세_조회시_참가자_memberId와_역할이_정확히_반환된다() {
        //given
        Trip trip = createTestTripWithParticipants(TripStatus.RECRUITING);

        //when
        TripDetailResponse tripDetail = tripService.getTripDetail(memberId, trip.getId());
        TripParticipantResponse leader = tripDetail.participants().stream()
                .filter(p -> p.role() == ParticipantRole.LEADER)
                .findFirst().orElseThrow();
        TripParticipantResponse participant = tripDetail.participants().stream()
                .filter(p -> p.role() == ParticipantRole.PARTICIPANT)
                .findFirst().orElseThrow();

        //then
        assertThat(leader.memberId()).isEqualTo(memberId);
        assertThat(participant.memberId()).isEqualTo(정수_ID);
    }

    @Test
    void 여행_상세_조회시_참여_신청_대기중이면_isPendingDemand가_true이다() {
        // given
        Trip trip = createTestTrip("테스트 여행", "여행 설명", TripCategory.DOMESTIC, TripStatus.RECRUITING);
        Demand demand = Demand.create(newMemberId, trip.getId(), "같이 여행 가고 싶어요!");
        demandRepository.save(demand);

        // when
        TripDetailResponse tripDetail = tripService.getTripDetail(newMemberId, trip.getId());

        // then
        assertThat(tripDetail.isPendingDemand()).isTrue();
        assertThat(tripDetail.isInvited()).isFalse();
        assertThat(tripDetail.pendingInvitationId()).isNull();
        assertThat(tripDetail.isParticipant()).isFalse();
        assertThat(tripDetail.isLeader()).isFalse();
    }

    @Test
    void 여행_상세_조회시_초대받은_상태이면_isInvited가_true이고_pendingInvitationId가_있다() {
        // given
        Trip trip = createTestTrip("테스트 여행", "여행 설명", TripCategory.DOMESTIC, TripStatus.RECRUITING);
        Invitation invitation = new Invitation(trip.getId(), newMemberId);
        invitationRepository.save(invitation);

        // when
        TripDetailResponse tripDetail = tripService.getTripDetail(newMemberId, trip.getId());

        // then
        assertThat(tripDetail.isInvited()).isTrue();
        assertThat(tripDetail.pendingInvitationId()).isEqualTo(invitation.getId());
        assertThat(tripDetail.isPendingDemand()).isFalse();
        assertThat(tripDetail.isParticipant()).isFalse();
        assertThat(tripDetail.isLeader()).isFalse();
    }

    @Test
    void 여행_상세_조회시_아무_관계없는_사용자는_상태_필드가_모두_false이다() {
        // given
        Trip trip = createTestTrip("테스트 여행", "여행 설명", TripCategory.DOMESTIC, TripStatus.RECRUITING);

        // when
        TripDetailResponse tripDetail = tripService.getTripDetail(newMemberId, trip.getId());

        // then
        assertThat(tripDetail.isLeader()).isFalse();
        assertThat(tripDetail.isParticipant()).isFalse();
        assertThat(tripDetail.isPendingDemand()).isFalse();
        assertThat(tripDetail.isInvited()).isFalse();
        assertThat(tripDetail.pendingInvitationId()).isNull();
    }

    @Test
    void 모집중_여행을_모집완료로_상태_변경한다() {
        // given
        Trip trip = createTestTrip("테스트 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING);

        // when
        tripService.toggleRecruitmentStatus(memberId, trip.getId());

        // then
        Trip updatedTrip = tripRepository.findById(trip.getId()).orElseThrow();
        assertThat(updatedTrip.getStatus()).isEqualTo(TripStatus.RECRUITMENT_CLOSED);
    }

    @Test
    void 모집완료_여행을_모집중으로_상태_변경한다() {
        // given
        Trip trip = createTestTrip("테스트 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITMENT_CLOSED);

        // when
        tripService.toggleRecruitmentStatus(memberId, trip.getId());

        // then
        Trip updatedTrip = tripRepository.findById(trip.getId()).orElseThrow();
        assertThat(updatedTrip.getStatus()).isEqualTo(TripStatus.RECRUITING);
    }

    @Test
    void 진행중_여행은_모집_상태를_변경할_수_없다() {
        // given
        Trip trip = createTestTrip("테스트 여행", "설명", TripCategory.DOMESTIC, TripStatus.IN_PROGRESS);

        // when & then
        assertThrows(BusinessException.class, () -> {
            tripService.toggleRecruitmentStatus(memberId, trip.getId());
        });
    }

    @Test
    void 완료된_여행은_모집_상태를_변경할_수_없다() {
        // given
        Trip trip = createTestTrip("테스트 여행", "설명", TripCategory.DOMESTIC, TripStatus.COMPLETED);

        // when & then
        assertThrows(BusinessException.class, () -> {
            tripService.toggleRecruitmentStatus(memberId, trip.getId());
        });
    }

    @Test
    void 리더가_아닌_멤버는_모집_상태를_변경할_수_없다() {
        // given
        Trip trip = createTestTrip("테스트 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING);
        trip.addParticipant(TripParticipant.createTripParticipant(newMemberId, trip));
        tripRepository.save(trip);

        // when & then
        assertThrows(MemberIsNotLeaderException.class, () -> {
            tripService.toggleRecruitmentStatus(newMemberId, trip.getId());
        });
    }

    @Test
    void 리더가_모집중_여행을_삭제한다() {
        // given
        Trip trip = createTestTrip("삭제할 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING);
        tripRepository.save(trip);

        // when
        tripService.deleteTrip(memberId, trip.getId());

        // then
        assertThat(tripRepository.findById(trip.getId())).isEmpty();
    }

    @Test
    void 리더가_모집완료_여행을_삭제한다() {
        // given
        Trip trip = createTestTrip("삭제할 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING);
        ReflectionTestUtils.setField(trip, "status", TripStatus.RECRUITMENT_CLOSED);
        tripRepository.save(trip);

        // when
        tripService.deleteTrip(memberId, trip.getId());

        // then
        assertThat(tripRepository.findById(trip.getId())).isEmpty();
    }

    @Test
    void 여행_삭제시_참가자_초대_신청이_모두_함께_삭제된다() {
        // given
        Trip trip = createTestTrip("삭제할 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING);
        trip.addParticipant(TripParticipant.createTripParticipant(newMemberId, trip));
        tripRepository.save(trip);

        invitationRepository.save(new com.retrip.trip.domain.entity.invitation.Invitation(trip.getId(), newMemberId));
        demandRepository.save(com.retrip.trip.domain.entity.demand.Demand.create(newMemberId, trip.getId(), "참여 요청"));

        // when
        tripService.deleteTrip(memberId, trip.getId());

        // then
        assertThat(tripRepository.findById(trip.getId())).isEmpty();
        assertThat(tripParticipantRepository.findById(newMemberId)).isEmpty();
        assertThat(invitationRepository.findByTripId(trip.getId())).isEmpty();
        assertThat(demandRepository.findAllByTripId(trip.getId())).isEmpty();
    }

    @Test
    void 리더가_아니면_여행을_삭제할_수_없다() {
        // given
        Trip trip = createTestTrip("삭제 불가 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING);
        trip.addParticipant(TripParticipant.createTripParticipant(newMemberId, trip));
        tripRepository.save(trip);

        // when & then
        assertThrows(MemberIsNotLeaderException.class, () ->
                tripService.deleteTrip(newMemberId, trip.getId()));
        assertThat(tripRepository.findById(trip.getId())).isPresent();
    }

    @Test
    void 여행중_상태에서는_여행을_삭제할_수_없다() {
        // given
        Trip trip = createTestTrip("여행중 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING);
        ReflectionTestUtils.setField(trip, "status", TripStatus.IN_PROGRESS);
        tripRepository.save(trip);

        // when & then
        assertThrows(BusinessException.class, () ->
                tripService.deleteTrip(memberId, trip.getId()));
        assertThat(tripRepository.findById(trip.getId())).isPresent();
    }

    @Test
    void 여행완료_상태에서는_여행을_삭제할_수_없다() {
        // given
        Trip trip = createTestTrip("완료된 여행", "설명", TripCategory.DOMESTIC, TripStatus.RECRUITING);
        ReflectionTestUtils.setField(trip, "status", TripStatus.COMPLETED);
        tripRepository.save(trip);

        // when & then
        assertThrows(BusinessException.class, () ->
                tripService.deleteTrip(memberId, trip.getId()));
        assertThat(tripRepository.findById(trip.getId())).isPresent();
    }

    @Test
    void 리더가_모집완료_상태를_여행중으로_강제_변경한다() {
        // given
        Trip trip = createReadyTrip(memberId); // RECRUITMENT_CLOSED

        // when
        tripService.forceChangeStatusToInProgress(memberId, trip.getId());

        // then
        Trip result = tripRepository.findById(trip.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(TripStatus.IN_PROGRESS);
    }

    @Test
    void 리더가_여행중_상태를_여행후로_강제_변경한다() {
        // given
        Trip trip = createProgressTrip(memberId); // IN_PROGRESS

        // when
        tripService.forceChangeStatusToCompleted(memberId, trip.getId());

        // then
        Trip result = tripRepository.findById(trip.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(TripStatus.COMPLETED);
    }

    @Test
    void 리더가_아니면_여행중으로_강제_변경할_수_없다() {
        // given: 정수_ID는 참여자이지만 리더가 아님
        Trip trip = createReadyTrip(memberId);
        trip.addParticipant(TripParticipant.createTripParticipant(정수_ID, trip));
        tripRepository.save(trip);

        // when & then
        assertThrows(MemberIsNotLeaderException.class,
                () -> tripService.forceChangeStatusToInProgress(정수_ID, trip.getId()));
    }

    @Test
    void 리더가_아니면_여행후로_강제_변경할_수_없다() {
        // given: 정수_ID는 참여자이지만 리더가 아님
        Trip trip = createProgressTrip(memberId);
        trip.addParticipant(TripParticipant.createTripParticipant(정수_ID, trip));
        tripRepository.save(trip);

        // when & then
        assertThrows(MemberIsNotLeaderException.class,
                () -> tripService.forceChangeStatusToCompleted(정수_ID, trip.getId()));
    }

    @Test
    void 모집완료가_아닌_상태에서_여행중으로_강제_변경할_수_없다() {
        // given: 이미 COMPLETED 상태
        Trip trip = createProgressTrip(memberId);
        ReflectionTestUtils.setField(trip, "status", TripStatus.COMPLETED);
        tripRepository.save(trip);

        // when & then
        assertThrows(BusinessException.class,
                () -> tripService.forceChangeStatusToInProgress(memberId, trip.getId()));
    }

    @Test
    void 여행중이_아닌_상태에서_여행후로_강제_변경할_수_없다() {
        // given: RECRUITMENT_CLOSED 상태
        Trip trip = createReadyTrip(memberId);

        // when & then
        assertThrows(BusinessException.class,
                () -> tripService.forceChangeStatusToCompleted(memberId, trip.getId()));
    }

    @Test
    void 여행_시작일이_지난_경우_모집완료에서_모집중으로_토글할_수_없다() {
        // given
        Trip trip = createReadyTrip(memberId); // RECRUITMENT_CLOSED, start = now + 1일
        ReflectionTestUtils.setField(trip.getPeriod(), "start", LocalDate.now().minusDays(1));
        tripRepository.save(trip);

        // when & then
        assertThrows(BusinessException.class,
                () -> tripService.toggleRecruitmentStatus(memberId, trip.getId()));
    }
}
