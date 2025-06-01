package com.retrip.trip.application.in;

import com.retrip.trip.application.in.base.BaseTripServiceTest;
import com.retrip.trip.application.in.request.PeriodUpdateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripDemandRequest;
import com.retrip.trip.application.in.request.TripFixture;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TripServiceTest extends BaseTripServiceTest {
    private TripPeriod createFuturePeriod() {
        return new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
    }

    private Trip createTestTrip(String title, String description, TripCategory category) {
        TripPeriod period = createFuturePeriod();
        Trip trip =
                Trip.create(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle(title),
                        new TripDescription(description),
                        period,
                        true,
                        4,
                        category);
        return tripRepository.save(trip);
    }

    @DisplayName("여행을 생성 한다.")
    @Test
    void createTrip() {
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

    @DisplayName("여행 목록을 조회한다.")
    @Test
    void getTrips() {
        TripPeriod period = createFuturePeriod();
        tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("속초 여행 멤버 구함"),
                        new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));
        tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("강릉 여행 멤버 구함"),
                        new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));
        tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("대구 여행 멤버 구함"),
                        new TripDescription("대구 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));
        tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
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
        Trip trip = createTestTrip("승인 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        TripDemandRequest request = new TripDemandRequest(newMemberId, "참여 요청 메시지");
        TripDemandResponse tripDemandResponse = tripService.tripDemand(trip.getId(), request);

        // then
        TripDemandApproveResponse response =
                tripService.approve(trip.getId(), tripDemandResponse.tripDemandId());

        // when
        assertThat(tripDemandResponse.status()).isEqualTo("대기");
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(TripDemandStatus.APPROVED.getCode());
    }

    @Test
    void 리더가_참여_요청을_거절하면_요청_상태가_거절로_변경된다() {
        // given
        Trip trip = createTestTrip("거절 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        TripDemandRequest request = new TripDemandRequest(newMemberId, "참여 요청 메시지");
        TripDemandResponse tripDemandResponse = tripService.tripDemand(trip.getId(), request);

        // then
        TripDemandRejectResponse response =
                tripService.reject(trip.getId(), tripDemandResponse.tripDemandId());

        // when
        assertThat(tripDemandResponse.status()).isEqualTo("대기");
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(TripDemandStatus.REJECTED.getCode());
    }

    @Test
    void 빈_여행_일정을_수정한다() {
        // given
        Trip trip = createTestTrip("테스트 여행", "여행 설명", TripCategory.DOMESTIC);

        // then
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);
        PeriodUpdateRequest request = TripFixture.createPeriod(
                memberId,
                start,
                end
        );
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
        TripPeriod period = new TripPeriod(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        Trip trip = tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("강릉 여행 멤버 구함"),
                        new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC)
        );


        // when
        PeriodUpdateRequest request = TripFixture.createPeriod(
                memberId,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );
        PeriodUpdateResponse response = tripService.updatePeriod(trip.getId(), request);

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
        Trip trip = tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("강릉 여행 멤버 구함"),
                        new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC)
        );


        // when
        PeriodUpdateRequest request = TripFixture.createPeriod(
                memberId,
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(8)
        );
        PeriodUpdateResponse response = tripService.updatePeriod(trip.getId(), request);

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
        Trip trip = tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("강릉 여행 멤버 구함"),
                        new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC)
        );


        // when

        PeriodUpdateRequest request = TripFixture.createPeriod(
                memberId,
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(8)
        );
        PeriodUpdateResponse response = tripService.updatePeriod(trip.getId(), request);

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
        Trip trip = tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("강릉 여행 멤버 구함"),
                        new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC)
        );


        // when
        PeriodUpdateRequest request = TripFixture.createPeriod(
                memberId,
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(9)
        );
        PeriodUpdateResponse response = tripService.updatePeriod(trip.getId(), request);

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
        Trip trip = tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("강릉 여행 멤버 구함"),
                        new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC)
        );


        // when
        PeriodUpdateRequest request = TripFixture.createPeriod(
                memberId,
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(12)
        );
        PeriodUpdateResponse response = tripService.updatePeriod(trip.getId(), request);

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
        Trip trip = tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("강릉 여행 멤버 구함"),
                        new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC)
        );


        // when
        PeriodUpdateRequest request = TripFixture.createPeriod(
                memberId,
                LocalDate.now().plusDays(11),
                LocalDate.now().plusDays(14)
        );
        PeriodUpdateResponse response = tripService.updatePeriod(trip.getId(), request);

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

}
