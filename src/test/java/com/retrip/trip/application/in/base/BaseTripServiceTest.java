package com.retrip.trip.application.in.base;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.TripService;
import com.retrip.trip.application.in.service.DemandService;
import com.retrip.trip.application.out.crypto.TripPasswordEncoder;
import com.retrip.trip.application.out.gateway.MemberGateway;
import com.retrip.trip.application.out.repository.*;
import com.retrip.trip.infra.adapter.out.crypto.TripBcryptTripPasswordEncoder;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.TripItineraryQuerydslRepository;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.TripQuerydslRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

public abstract class BaseTripServiceTest extends BaseServiceTest {
    @Autowired
    protected TripRepository tripRepository;

    @Autowired
    protected DemandRepository demandRepository;

    @Autowired
    protected InvitationRepository invitationRepository;

    @Autowired
    protected VoteRepository voteRepository;

    @Autowired
    protected EntityManager em;

    @Autowired
    protected TripConfirmationDemandRepository tripConfirmationDemandRepository;

    @Autowired
    protected JPAQueryFactory jpaQueryFactory;

    @Autowired
    protected MemberGateway memberGateway;

    protected TripService tripService;
    protected DemandService demandService;
    protected TripQueryRepository tripQueryRepository;
    protected TripItineraryQueryRepository tripItineraryQueryRepository;

    protected UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    protected UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");
    protected UUID newMemberId = UUID.fromString("11111111-2222-3333-4444-555555555555");

    @BeforeEach
    void setUp() {
        tripRepository.deleteAll();
        demandRepository.deleteAll();

        tripQueryRepository = new TripQuerydslRepository(jpaQueryFactory);
        tripItineraryQueryRepository = new TripItineraryQuerydslRepository(jpaQueryFactory);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        TripPasswordEncoder tripPasswordEncoder = new TripBcryptTripPasswordEncoder(bCryptPasswordEncoder);

        tripService = new TripService(
                tripRepository, tripQueryRepository, tripItineraryQueryRepository,
                tripPasswordEncoder, memberGateway, demandRepository, invitationRepository, voteRepository
        );
    }
}
