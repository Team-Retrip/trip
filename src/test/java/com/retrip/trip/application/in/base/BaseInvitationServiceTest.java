package com.retrip.trip.application.in.base;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.service.InvitationService;
import com.retrip.trip.application.out.repository.InvitationRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.service.InvitationPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseInvitationServiceTest extends BaseServiceTest {
    @Autowired
    protected TripRepository tripRepository;

    @Autowired
    protected InvitationRepository invitationRepository;
    protected InvitationPolicy invitationPolicy = new InvitationPolicy();

    @Autowired
    protected JPAQueryFactory jpaQueryFactory;
    protected InvitationService invitationService;

    @BeforeEach
    void setUp() {
        invitationService = new InvitationService(tripRepository, invitationRepository, invitationPolicy);
    }
}
