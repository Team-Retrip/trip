package com.retrip.trip.application.in.base;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.service.DemandService;
import com.retrip.trip.application.out.gateway.AlarmGateway;
import com.retrip.trip.application.out.gateway.MemberGateway;
import com.retrip.trip.application.out.repository.DemandRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.service.DemandPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseDemandServiceTest extends BaseServiceTest {
    @Autowired
    protected TripRepository tripRepository;
    @Autowired
    protected DemandRepository demandRepository;
    protected DemandPolicy demandPolicy = new DemandPolicy();

    @Autowired
    protected JPAQueryFactory jpaQueryFactory;
    protected DemandService demandService;

    @Autowired
    protected AlarmGateway alarmGateway;

    @Autowired
    protected MemberGateway memberGateway;

    @BeforeEach
    void setUp() {
        demandRepository.deleteAll();
        tripRepository.deleteAll();
        demandService = new DemandService(tripRepository, demandRepository, demandPolicy, alarmGateway, memberGateway);
    }
}
