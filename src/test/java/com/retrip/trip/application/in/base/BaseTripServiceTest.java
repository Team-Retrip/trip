package com.retrip.trip.application.in.base;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.TripService;
import com.retrip.trip.application.out.repository.*;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.TripItineraryQuerydslRepository;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.TripQuerydslRepository;
import com.retrip.trip.infra.config.QuerydslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseTripServiceTest {
    @Autowired
    protected TripRepository tripRepository;

    @Autowired
    protected TripDemandReadRepository tripDemandReadRepository;

    @Autowired
    protected TripParticipantRepository tripParticipantRepository;

    @Autowired
    protected JPAQueryFactory jpaQueryFactory;

    protected TripService tripService;
    protected TripQueryRepository tripQueryRepository;
    protected TripItineraryQueryRepository tripItineraryQueryRepository;

    protected UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    protected UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");
    protected UUID newMemberId = UUID.fromString("11111111-2222-3333-4444-555555555555");

    @BeforeEach
    void setUp() {
        tripQueryRepository = new TripQuerydslRepository(jpaQueryFactory);
        tripItineraryQueryRepository = new TripItineraryQuerydslRepository(jpaQueryFactory);

        tripService =
                new TripService(
                        tripRepository, tripQueryRepository, tripItineraryQueryRepository, tripDemandReadRepository, tripParticipantRepository);
    }
}
