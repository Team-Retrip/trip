package com.retrip.trip.application.in.base;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.service.ParticipantService;
import com.retrip.trip.application.out.crypto.TripPasswordEncoder;
import com.retrip.trip.application.out.repository.ParticipantRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.infra.adapter.out.crypto.TripBcryptTripPasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public abstract class BaseParticipantServiceTest extends BaseServiceTest {
    @Autowired
    protected TripRepository tripRepository;

    @Autowired
    protected ParticipantRepository participantRepository;

    @Autowired
    protected JPAQueryFactory jpaQueryFactory;
    protected ParticipantService participantService;
    protected TripPasswordEncoder tripPasswordEncoder;

    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        tripPasswordEncoder = new TripBcryptTripPasswordEncoder(bCryptPasswordEncoder);
        participantService = new ParticipantService(tripRepository, participantRepository, tripPasswordEncoder);
    }
}
