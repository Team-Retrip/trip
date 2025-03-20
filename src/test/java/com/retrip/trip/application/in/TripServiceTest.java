package com.retrip.trip.application.in;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripJoinRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.response.TripJoinResponse;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.application.out.repository.JoinRequestRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
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
    JoinRequestRepository joinRequestRepository;

    TripService tripService;
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");
    UUID newMemberId = UUID.fromString("11111111-2222-3333-4444-555555555555");

    @BeforeEach
    void setUp() {
        tripService = new TripService(tripRepository, tripQueryRepository, joinRequestRepository);
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
        TripPeriod period = new TripPeriod(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );
        tripRepository.save(Trip.createWithItineraries(memberId,UUID.randomUUID(),new TripTitle("속초 여행 멤버 구함"), new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"), period, true, 4, TripCategory.DOMESTIC));
        tripRepository.save(Trip.createWithItineraries(memberId,UUID.randomUUID(),new TripTitle("강릉 여행 멤버 구함"), new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"), period, true, 4, TripCategory.DOMESTIC));
        tripRepository.save(Trip.createWithItineraries(memberId,UUID.randomUUID(),new TripTitle("대구 여행 멤버 구함"), new TripDescription("대구 여행은 이렇게이렇게 갈겁니다~"), period, true, 4, TripCategory.DOMESTIC));
        tripRepository.save(Trip.createWithItineraries(memberId,UUID.randomUUID(),new TripTitle("부산 여행 멤버 구함"), new TripDescription("부산 여행은 이렇게이렇게 갈겁니다~"), period, true, 4, TripCategory.DOMESTIC));

        Page<TripResponse> trips = tripService.getTrips(PageRequest.of(0, 2));

        assertThat(trips.getTotalElements()).isEqualTo(2);
        assertThat(trips.getPageable().getOffset()).isEqualTo(0);
        assertThat(trips.getPageable().getPageSize()).isEqualTo(2);
    }

    @DisplayName("여행의 일정 목록을 생성 한다.")
    @Test
    void createItineraries() {
        TripPeriod period = new TripPeriod(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );
        Trip trip = tripRepository.save(Trip.createWithItineraries(memberId,UUID.randomUUID(),new TripTitle("속초 여행 멤버 구함"), new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"), period, true, 4, TripCategory.DOMESTIC));
        List<ItinerariesCreateRequest.ItineraryCreateRequest> itineraries = List.of(
                new ItinerariesCreateRequest.ItineraryCreateRequest(LocalDate.now().plusDays(2)),
                new ItinerariesCreateRequest.ItineraryCreateRequest(LocalDate.now().plusDays(3)),
                new ItinerariesCreateRequest.ItineraryCreateRequest(LocalDate.now().plusDays(4))        );

        ItinerariesCreateRequest request = new ItinerariesCreateRequest(trip.getId(), itineraries);

        ItinerariesCreateResponse response = tripService.createItineraries(request);
        assertThat(response.tripId()).isNotNull();
        assertThat(response.itineraries().size()).isEqualTo(3);
    }
    @DisplayName("사용자가 참여 요청을 보낸다.")
    @Test
    void joinTrip() {
        TripPeriod period = new TripPeriod(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );
        Trip trip = Trip.create(memberId, UUID.randomUUID(),
                new TripTitle("테스트 여행"), new TripDescription("여행 설명"),
                period, true, 4, TripCategory.DOMESTIC);
        trip = tripRepository.save(trip);

        TripJoinRequest joinRequest = new TripJoinRequest(trip.getId(), newMemberId, "참여 요청 메시지");
        TripJoinResponse joinResponse = tripService.joinTrip(joinRequest);
        assertThat(joinResponse).isNotNull();
        assertThat(joinResponse.tripId()).isEqualTo(trip.getId());
        assertThat(joinResponse.memberId()).isEqualTo(newMemberId);
        assertThat(joinResponse.status()).isEqualTo("대기");
    }

    @DisplayName("리더가 참여 요청을 승인하면 실제 참여자로 등록된다.")
    @Test
    void approveJoinRequest() {
        TripPeriod period = new TripPeriod(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );
        Trip trip = Trip.create(memberId, UUID.randomUUID(),
                new TripTitle("승인 테스트 여행"), new TripDescription("여행 설명"),
                period, true, 4, TripCategory.DOMESTIC);
        trip = tripRepository.save(trip);

        TripJoinRequest joinRequest = new TripJoinRequest(trip.getId(), newMemberId, "참여 요청 메시지");
        TripJoinResponse joinResponse = tripService.joinTrip(joinRequest);
        assertThat(joinResponse.status()).isEqualTo("대기");

        final Trip savedTrip = trip;
        UUID joinRequestId = joinRequestRepository.findAll().stream()
                .filter(jr -> jr.getTrip().getId().equals(savedTrip.getId())
                        && jr.getUserId().equals(newMemberId))
                .findFirst()
                .orElseThrow()
                .getId();

        TripParticipant approvedParticipant = tripService.approveJoinRequest(trip.getId(), joinRequestId);
        assertThat(approvedParticipant).isNotNull();
        assertThat(approvedParticipant.getTrip().getId()).isEqualTo(trip.getId());
        assertThat(approvedParticipant.getUserId()).isEqualTo(newMemberId);
        assertThat(approvedParticipant.getStatus().getViewName()).isEqualTo("승인");
    }

    @DisplayName("리더가 참여 요청을 거절하면 요청 상태가 거절로 변경된다.")
    @Test
    void rejectJoinRequest() {
        TripPeriod period = new TripPeriod(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );
        Trip trip = Trip.create(memberId, UUID.randomUUID(),
                new TripTitle("거절 테스트 여행"), new TripDescription("여행 설명"),
                period, true, 4, TripCategory.DOMESTIC);
        trip = tripRepository.save(trip);

        TripJoinRequest joinRequest = new TripJoinRequest(trip.getId(), newMemberId, "참여 요청 메시지");
        TripJoinResponse joinResponse = tripService.joinTrip(joinRequest);
        assertThat(joinResponse.status()).isEqualTo("대기");

        final Trip savedTrip = trip;
        UUID joinRequestId = joinRequestRepository.findAll().stream()
                .filter(jr -> jr.getTrip().getId().equals(savedTrip.getId())
                        && jr.getUserId().equals(newMemberId))
                .findFirst()
                .orElseThrow()
                .getId();

        TripJoinResponse rejectedResponse = tripService.rejectJoinRequest(trip.getId(), joinRequestId);
        assertThat(rejectedResponse).isNotNull();
        assertThat(rejectedResponse.status()).isEqualTo("거절");
    }
}