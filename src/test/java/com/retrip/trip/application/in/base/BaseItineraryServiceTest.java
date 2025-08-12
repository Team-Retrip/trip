package com.retrip.trip.application.in.base;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.ItineraryService;
import com.retrip.trip.application.out.repository.TripItineraryQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.fixture.TripFixture;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.TripItineraryQuerydslRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.UUID;

public abstract class BaseItineraryServiceTest extends BaseServiceTest {
    @Autowired
    protected TripRepository tripRepository;
    @Autowired
    protected JPAQueryFactory jpaQueryFactory;
    protected TripItineraryQueryRepository tripItineraryQueryRepository;


    protected UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    protected UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");
    protected ItineraryService itineraryService;

    protected Trip trip;

    @BeforeEach
    void setUp() {
        tripItineraryQueryRepository = new TripItineraryQuerydslRepository(jpaQueryFactory);
        itineraryService = new ItineraryService(tripItineraryQueryRepository);
        trip = TripFixture.createTestTripWithPeriod(memberId, "속초 여행 맴버 구함", "속초 여행은 이렇게이렇게 갈겁니다~", TripCategory.DOMESTIC,
                new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)));
    }
}
