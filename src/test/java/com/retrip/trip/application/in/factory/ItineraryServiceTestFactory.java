package com.retrip.trip.application.in.factory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.application.in.ItineraryService;
import com.retrip.trip.application.out.repository.ItineraryQueryRepository;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.ItineraryQuerydslRepository;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.TripQuerydslRepository;
import com.retrip.trip.infra.config.QuerydslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class ItineraryServiceTestFactory {
    @Autowired protected TripRepository tripRepository;

    @Autowired protected JPAQueryFactory jpaQueryFactory;

    protected ItineraryQueryRepository itineraryQueryRepository;

    protected TripQueryRepository tripQueryRepository;

    protected ItineraryService itineraryService;
    protected UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    protected UUID 속초_해수욕장_Id = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c004");
    protected UUID 속초_중앙_시장_Id = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c005");
    protected UUID 숙소_Id = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c006");
    protected UUID 집_Id = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c007");
    protected LocalDate start = LocalDate.now().plusDays(1);
    protected LocalDate end = start.plusDays(10);

    @BeforeEach
    void setUp() {
        itineraryQueryRepository = new ItineraryQuerydslRepository(jpaQueryFactory);
        tripQueryRepository = new TripQuerydslRepository(jpaQueryFactory);
        itineraryService = new ItineraryService(tripQueryRepository, itineraryQueryRepository);
    }
}
