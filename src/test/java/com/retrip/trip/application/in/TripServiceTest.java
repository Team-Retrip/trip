package com.retrip.trip.application.in;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripDemandRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.response.TripDemandApproveResponse;
import com.retrip.trip.application.in.response.TripDemandRejectResponse;
import com.retrip.trip.application.in.response.TripDemandResponse;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.application.out.repository.TripParticipantRepository;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.application.out.repository.TripDemandRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripDemandStatus;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.TripQuerydslRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TripServiceTest {

    @Autowired
    TripRepository tripRepository;

    @Autowired
    TripQueryRepository tripQueryRepository;

    @Autowired
    TripDemandRepository tripDemandRepository;

    @Autowired
    TripParticipantRepository tripParticipantRepository;

    TripService tripService;
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");
    UUID newMemberId = UUID.fromString("11111111-2222-3333-4444-555555555555");

    @BeforeEach
    void setUp() {
        tripService = new TripService(tripRepository, tripQueryRepository, tripDemandRepository, tripParticipantRepository);
    }

    @TestConfiguration
    static class QuerydslConfig {
        @Autowired
        EntityManager entityManager;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(entityManager);
        }

        @Bean
        public TripQuerydslRepository tripQuerydslRepository(JPAQueryFactory jpaQueryFactory) {
            return new TripQuerydslRepository(jpaQueryFactory);
        }
    }

    // 헬퍼: 미래의 여행 기간 생성 (시작일: 현재 +1일, 종료일: 현재 +5일)
    private TripPeriod createFuturePeriod() {
        return new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
    }

    // 헬퍼: 테스트용 Trip 생성
    private Trip createTestTrip(String title, String description, TripCategory category) {
        TripPeriod period = createFuturePeriod();
        Trip trip = Trip.create(memberId, UUID.randomUUID(), new TripTitle(title), new TripDescription(description), period, true, 4, category);
        return tripRepository.save(trip);
    }

    @DisplayName("여행을 생성 한다.")
    @Test
    void createTrip() {
        TripCreateRequest request = new TripCreateRequest(
                memberId,
                locationId,
                "속초 여행 멤버 구함",
                "속초 여행은 이렇게이렇게 갈겁니다~",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                true,
                4,
                TripCategory.DOMESTIC
        );
        TripCreateResponse response = tripService.createTrip(request);
        assertThat(response.id()).isNotNull();
        assertThat(response.destinationId()).isEqualTo(locationId);
    }

    @DisplayName("여행 목록을 조회한다.")
    @Test
    void getTrips() {
        TripPeriod period = createFuturePeriod();
        tripRepository.save(Trip.createWithItineraries(memberId, UUID.randomUUID(),
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                period, true, 4, TripCategory.DOMESTIC));
        tripRepository.save(Trip.createWithItineraries(memberId, UUID.randomUUID(),
                new TripTitle("강릉 여행 멤버 구함"),
                new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                period, true, 4, TripCategory.DOMESTIC));
        tripRepository.save(Trip.createWithItineraries(memberId, UUID.randomUUID(),
                new TripTitle("대구 여행 멤버 구함"),
                new TripDescription("대구 여행은 이렇게이렇게 갈겁니다~"),
                period, true, 4, TripCategory.DOMESTIC));
        tripRepository.save(Trip.createWithItineraries(memberId, UUID.randomUUID(),
                new TripTitle("부산 여행 멤버 구함"),
                new TripDescription("부산 여행은 이렇게이렇게 갈겁니다~"),
                period, true, 4, TripCategory.DOMESTIC));

        Page<TripResponse> trips = tripService.getTrips(PageRequest.of(0, 2));
        assertThat(trips.getTotalElements()).isEqualTo(2);
        assertThat(trips.getPageable().getOffset()).isEqualTo(0);
        assertThat(trips.getPageable().getPageSize()).isEqualTo(2);
    }

    @DisplayName("여행의 일정 목록을 생성 한다.")
    @Test
    void createItineraries() {
        Trip trip = createTestTrip("속초 여행 멤버 구함", "속초 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC);
        List<ItinerariesCreateRequest.ItineraryCreateRequest> itineraries = List.of(
                new ItinerariesCreateRequest.ItineraryCreateRequest(LocalDate.now().plusDays(2)),
                new ItinerariesCreateRequest.ItineraryCreateRequest(LocalDate.now().plusDays(3)),
                new ItinerariesCreateRequest.ItineraryCreateRequest(LocalDate.now().plusDays(4))
        );
        ItinerariesCreateRequest request = new ItinerariesCreateRequest(trip.getId(), itineraries);
        ItinerariesCreateResponse response = tripService.createItineraries(request);
        assertThat(response.tripId()).isNotNull();
        assertThat(response.itineraries().size()).isEqualTo(3);
    }

    @Test
    void 사용자가_참여_요청을_보낸다() {
        //given
        Trip trip = createTestTrip("테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        TripDemandRequest request = new TripDemandRequest(newMemberId, "참여 요청 메시지");

        //when
        TripDemandResponse response = tripService.tripDemand(trip.getId(), request);

        //then
        assertThat(response).isNotNull();
        assertThat(response.tripId()).isEqualTo(trip.getId());
        assertThat(response.memberId()).isEqualTo(newMemberId);
        assertThat(response.status()).isEqualTo("대기");
    }

    @Test
    void 리더가_참여_요청을_승인하면_실제_참여자로_등록된다() {
        //given
        Trip trip = createTestTrip("승인 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        TripDemandRequest request = new TripDemandRequest(newMemberId, "참여 요청 메시지");
        TripDemandResponse tripDemandResponse = tripService.tripDemand(trip.getId(), request);

        //then
        TripDemandApproveResponse response = tripService.approve(trip.getId(), tripDemandResponse.tripDemandId());

        //when
        assertThat(tripDemandResponse.status()).isEqualTo("대기");
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(TripDemandStatus.APPROVED.getCode());
    }

    @Test
    void 리더가_참여_요청을_거절하면_요청_상태가_거절로_변경된다() {
        //given
        Trip trip = createTestTrip("거절 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        TripDemandRequest request = new TripDemandRequest(newMemberId, "참여 요청 메시지");
        TripDemandResponse tripDemandResponse = tripService.tripDemand(trip.getId(), request);

        //then
        TripDemandRejectResponse response = tripService.reject(trip.getId(), tripDemandResponse.tripDemandId());

        //when
        assertThat(tripDemandResponse.status()).isEqualTo("대기");
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(TripDemandStatus.REJECTED.getCode());
    }
}
