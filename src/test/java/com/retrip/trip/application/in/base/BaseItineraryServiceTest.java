package com.retrip.trip.application.in.base;

import com.retrip.trip.application.in.ItineraryService;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
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
public abstract class BaseItineraryServiceTest {
  @Autowired protected TripRepository tripRepository;

  protected UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
  protected ItineraryService itineraryService;

  protected Trip trip =
      Trip.create(
          memberId,
          UUID.randomUUID(),
          new TripTitle("속초 여행 멤버 구함"),
          new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
          new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)),
          true,
          4,
          TripCategory.DOMESTIC);

  @BeforeEach
  void setUp() {
    itineraryService = new ItineraryService(tripRepository);
  }
}
