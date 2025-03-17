package com.retrip.trip.application.in;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.out.repository.ItineraryQueryRepository;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.ItineraryDetail;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.ItineraryQuerydslRepository;
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
class ItineraryServiceTest {
    @Autowired
    TripRepository tripRepository;

    @Autowired
    ItineraryQueryRepository itineraryQueryRepository;
    @Autowired
    TripQueryRepository tripQueryRepository;


    ItineraryService itineraryService;
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID 속초_해수욕장_Id = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c004");
    UUID 속초_중앙_시장_Id = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c006");
    LocalDate start = LocalDate.now().plusDays(1);
    LocalDate end = start.plusDays(10);

    @BeforeEach
    void setUp() {
        itineraryService = new ItineraryService(tripQueryRepository, itineraryQueryRepository);
    }

    @TestConfiguration
    static class QuerydslConfig {
        @Autowired
        EntityManager entityManager;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
        }

        @Bean
        public ItineraryQuerydslRepository itineraryQuerydslRepository(JPAQueryFactory jpaQueryFactory) {
            return new ItineraryQuerydslRepository(jpaQueryFactory);
        }

        @Bean
        public TripQuerydslRepository tripQuerydslRepository(JPAQueryFactory jpaQueryFactory) {
            return new TripQuerydslRepository(jpaQueryFactory);
        }
    }


    @DisplayName("여행의 일정 목록을 수정 한다.")
    @Test
    void updateItineraries() {
        TripPeriod period = new TripPeriod(
                start,
                end
        );
        Trip trip = tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("속초 여행 멤버 구함"),
                        new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC
                ));

        List<ItinerariesUpdateRequest.ItineraryUpdateRequest> itineraries = List.of(
                new ItinerariesUpdateRequest.ItineraryUpdateRequest(start.plusDays(2), null),
                new ItinerariesUpdateRequest.ItineraryUpdateRequest(start.plusDays(3), null),
                new ItinerariesUpdateRequest.ItineraryUpdateRequest(
                        start.plusDays(4),
                        List.of(
                                new ItinerariesUpdateRequest.ItineraryUpdateRequest.ItineraryDetailUpdateRequest(
                                        10000L, 속초_해수욕장_Id, "해수욕장에서 사진 및 카페 사진 찍기"
                                ),
                                new ItinerariesUpdateRequest.ItineraryUpdateRequest.ItineraryDetailUpdateRequest(
                                        50000L, 속초_중앙_시장_Id, "속초 중앙 시장에서 오징어 순대, 닭강정 먹기"
                                )
                        )
                ),
                new ItinerariesUpdateRequest.ItineraryUpdateRequest(start.plusDays(5), null)
        );

        ItinerariesUpdateRequest request = new ItinerariesUpdateRequest(trip.getId(), memberId, itineraries);

        ItinerariesUpdateResponse response = itineraryService.updateItineraries(request);
        assertThat(response.tripId()).isNotNull();
        assertThat(response.itineraries().size()).isEqualTo(4);
        assertThat(response.itineraries().get(2).itineraryDetails().size()).isEqualTo(2);
    }

    @DisplayName("여행 일정을 조회한다.")
    @Test
    void getItineraries() {
        TripPeriod period = new TripPeriod(
                start,
                end
        );
        Trip trip = tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("속초 여행 멤버 구함"),
                        new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC
                )
        );
        List<Itinerary> itineraries = List.of(
                Itinerary.create(trip, start),
                Itinerary.create(trip, start.plusDays(1)),
                Itinerary.create(trip, end.minusDays(1))
        );
        itineraries.getFirst().createItineraryDetails(List.of(
                ItineraryDetail.create(10000L, "해수욕장에서 사진 및 카페 사진 찍기", itineraries.getFirst(), 속초_해수욕장_Id),
                ItineraryDetail.create(50000L, "속초 중앙 시장에서 오징어 순대, 닭강정 먹기", itineraries.getFirst(), 속초_중앙_시장_Id)
        ));
        itineraries.getLast().createItineraryDetails(List.of(
                ItineraryDetail.create(0L, "집가기", itineraries.getLast(), null)
        ));
        trip.updateItineraries(itineraries, memberId);

        Page<ItineraryResponse> searchItineraryResponses = itineraryService.getItineraries(trip.getId(), PageRequest.of(0, 14));

        assertThat(searchItineraryResponses.getTotalElements()).isEqualTo(14);
        assertThat(searchItineraryResponses.getPageable().getOffset()).isEqualTo(0);
        assertThat(searchItineraryResponses.getPageable().getPageSize()).isEqualTo(14);
    }
}
