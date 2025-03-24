package com.retrip.trip.application.in;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;

import com.retrip.trip.infra.adapter.out.persistence.mysql.query.TripQuerydslRepository;
import com.retrip.trip.infra.config.QuerydslConfig;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class TripServiceTestFactory {
    @Autowired
    TripRepository tripRepository;
    @Autowired
    JPAQueryFactory jpaQueryFactory;
    TripQueryRepository tripQueryRepository;

    TripService tripService;
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");
    LocalDate start = LocalDate.now().plusDays(1);
    LocalDate end = start.plusDays(10);


    @BeforeEach
    void setUp() {
        tripQueryRepository = new TripQuerydslRepository(jpaQueryFactory);
        tripService = new TripService(tripRepository, tripQueryRepository);
    }

}
