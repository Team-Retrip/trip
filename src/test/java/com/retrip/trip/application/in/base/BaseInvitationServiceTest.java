package com.retrip.trip.application.in.base;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.service.InvitationService;
import com.retrip.trip.application.in.service.ParticipantService;
import com.retrip.trip.application.in.service.TripService;
import com.retrip.trip.application.out.repository.InvitationRepository;
import com.retrip.trip.application.out.repository.ParticipantQueryRepository;
import com.retrip.trip.application.out.repository.ParticipantRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.service.InvitationPolicy;
import com.retrip.trip.domain.service.ParticipantPolicy;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.ParticipantQuerydslRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseInvitationServiceTest extends BaseServiceTest {
    @Autowired protected TripRepository tripRepository;

    @Autowired protected InvitationRepository invitationRepository;
    protected InvitationPolicy invitationPolicy = new InvitationPolicy();

    @Autowired protected JPAQueryFactory jpaQueryFactory;
    protected InvitationService invitationService;

    protected ParticipantService participantService;
    protected ParticipantPolicy participantPolicy = new ParticipantPolicy();
    @Autowired protected ParticipantRepository participantRepository;
    protected ParticipantQueryRepository participantQueryRepository;

    @BeforeEach
    void setUp() {
        participantQueryRepository = new ParticipantQuerydslRepository(jpaQueryFactory);
        participantService =
                new ParticipantService(
                        participantPolicy, participantRepository, participantQueryRepository);
        invitationService =
                new InvitationService(
                        tripRepository, invitationRepository, participantService, invitationPolicy);
    }
}
