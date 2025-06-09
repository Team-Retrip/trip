package com.retrip.trip.application.in.base;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.TripInvitationService;
import com.retrip.trip.application.out.repository.TripInvitationReadRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseTripInvitationServiceTest extends BaseServiceTest {
    @Autowired
    protected TripRepository tripRepository;

    @Autowired
    protected TripInvitationReadRepository tripInvitationReadRepository;

    @Autowired
    protected JPAQueryFactory jpaQueryFactory;

    protected TripInvitationService tripInvitationService;


    @BeforeEach
    void setUp() {
        tripInvitationService = new TripInvitationService(tripRepository, tripInvitationReadRepository);
    }
}
