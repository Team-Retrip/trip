package com.retrip.trip.application.out.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;

import java.time.LocalDate;
import java.util.List;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReadRepositoryTest {
    @Autowired private ReadTestRepository repository;

    @Autowired private TripRepository tripRepository;

    @Test
    public void 네임드_쿼리_테스트() {
        // given
        TripPeriod period =
                new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
        UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");

        tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("속초 여행 멤버 구함"),
                        new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));
        tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("강릉 여행 멤버 구함"),
                        new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));
        tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("대구 여행 멤버 구함"),
                        new TripDescription("대구 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));
        tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("부산 여행 멤버 구함"),
                        new TripDescription("부산 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        false,
                        4,
                        TripCategory.DOMESTIC));

        // when
        List<Trip> result = repository.findByOpenTrue();

        // then
        assertEquals(result.size(), 3);
    }

    @Test
    public void SAVA_불가능_테스트() {
        // given
        TripPeriod period =
                new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
        UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");

        assertThatThrownBy(
                        () -> {
                            repository.save(
                                    Trip.createWithItineraries(
                                            memberId,
                                            UUID.randomUUID(),
                                            new TripTitle("속초 여행 멤버 구함"),
                                            new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                                            period,
                                            true,
                                            4,
                                            TripCategory.DOMESTIC));
                        })
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }
}
