package com.retrip.trip.application.in;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripPeriod;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TripServiceTest {
    @Autowired
    TripRepository tripRepository;

    @Autowired
    TripQueryRepository tripQueryRepository;
    TripService tripService;
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");

    @BeforeEach
    void setUp() {
        tripService = new TripService(tripRepository, tripQueryRepository);
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
                "속초 여행 멤버 구함",
                locationId,
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2025, 3, 15),
                true
        );
        TripCreateResponse response = tripService.createTrip(request);
        assertThat(response.id()).isNotNull();
        assertThat(response.leaderId()).isEqualTo(memberId);
        assertThat(response.destinationId()).isEqualTo(locationId);
    }

    @DisplayName("여행 목록을 조회한다.")
    @Test
    void getTrips() {
        TripPeriod period = new TripPeriod(
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2025, 3, 15)
        );
        tripRepository.save(Trip.createWithItinerary("속초 여행 멤버 구함", UUID.randomUUID(), period, true, memberId));
        tripRepository.save(Trip.createWithItinerary("강릉 여행 멤버 구함", UUID.randomUUID(), period, true, memberId));
        tripRepository.save(Trip.createWithItinerary("대구 여행 멤버 구함", UUID.randomUUID(), period, true, memberId));
        tripRepository.save(Trip.createWithItinerary("부산 여행 멤버 구함", UUID.randomUUID(), period, true, memberId));

        Page<TripResponse> trips = tripService.getTrips(PageRequest.of(0, 2));

        assertThat(trips.getTotalElements()).isEqualTo(2);
        assertThat(trips.getPageable().getOffset()).isEqualTo(0);
        assertThat(trips.getPageable().getPageSize()).isEqualTo(2);
    }
}
