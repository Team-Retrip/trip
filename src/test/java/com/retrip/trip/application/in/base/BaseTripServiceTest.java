package com.retrip.trip.application.in.base;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.TripService;
import com.retrip.trip.application.out.repository.TripDemandRepository;
import com.retrip.trip.application.out.repository.TripParticipantRepository;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.TripQuerydslRepository;
import com.retrip.trip.infra.config.QuerydslConfig;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseTripServiceTest {
  @Autowired protected TripRepository tripRepository;

  @Autowired protected TripDemandRepository tripDemandRepository;

  @Autowired protected TripParticipantRepository tripParticipantRepository;

  @Autowired protected JPAQueryFactory jpaQueryFactory;

  protected TripService tripService;
  protected TripQueryRepository tripQueryRepository;

  protected UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
  protected UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");
  protected UUID newMemberId = UUID.fromString("11111111-2222-3333-4444-555555555555");

  @BeforeEach
  void setUp() {
    tripQueryRepository = new TripQuerydslRepository(jpaQueryFactory);

    tripService =
        new TripService(
            tripRepository, tripQueryRepository, tripDemandRepository, tripParticipantRepository);
  }
}
