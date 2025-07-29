package com.retrip.trip.application.in.base;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.service.ParticipantService;
import com.retrip.trip.application.in.service.TripService;
import com.retrip.trip.application.out.repository.*;
import com.retrip.trip.domain.service.ParticipantPolicy;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.ParticipantQuerydslRepository;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.TripItineraryQuerydslRepository;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.TripQuerydslRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public abstract class BaseTripServiceTest extends BaseServiceTest {
    @Autowired protected TripRepository tripRepository;

    @Autowired protected EntityManager em;

    @Autowired protected TripDemandReadRepository tripDemandReadRepository;
    @Autowired protected ParticipantRepository participantRepository;
    protected ParticipantQueryRepository participantQueryRepository;

    @Autowired protected JPAQueryFactory jpaQueryFactory;

    protected TripService tripService;
    protected ParticipantPolicy participantPolicy;
    protected ParticipantService participantService;
    protected TripQueryRepository tripQueryRepository;
    protected TripItineraryQueryRepository tripItineraryQueryRepository;

    protected UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    protected UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");
    protected UUID newMemberId = UUID.fromString("11111111-2222-3333-4444-555555555555");

    @BeforeEach
    void setUp() {
        tripQueryRepository = new TripQuerydslRepository(jpaQueryFactory);
        tripItineraryQueryRepository = new TripItineraryQuerydslRepository(jpaQueryFactory);
        participantPolicy = new ParticipantPolicy();
        participantQueryRepository = new ParticipantQuerydslRepository(jpaQueryFactory);
        participantService =
                new ParticipantService(
                        participantPolicy, participantRepository, participantQueryRepository);
        tripService =
                new TripService(
                        tripRepository,
                        tripQueryRepository,
                        tripItineraryQueryRepository,
                        tripDemandReadRepository,
                        participantService);
    }
}
