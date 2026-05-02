package com.retrip.trip.infra.adapter.in.scheduler;

import com.retrip.trip.application.in.base.BaseServiceTest;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.fixture.TripFixture;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static com.retrip.trip.domain.fixture.TripFixture.LEADER_ID;
import static org.assertj.core.api.Assertions.assertThat;

class TripStatusSchedulerTest extends BaseServiceTest {

    @Autowired
    private TripRepository tripRepository;

    private TripStatusScheduler scheduler;

    @BeforeEach
    void setUp() {
        tripRepository.deleteAll();
        scheduler = new TripStatusScheduler(tripRepository);
    }

    private Trip createTripWithPeriod(TripStatus status, LocalDate start, LocalDate end) {
        Trip trip = TripFixture.createTestTrip(LEADER_ID, "테스트 여행", "설명", TripCategory.DOMESTIC);
        ReflectionTestUtils.setField(trip, "status", status);
        ReflectionTestUtils.setField(trip.getPeriod(), "start", start);
        ReflectionTestUtils.setField(trip.getPeriod(), "end", end);
        return tripRepository.save(trip);
    }

    @Test
    void RECRUITMENT_CLOSED_여행이_시작일에_IN_PROGRESS로_변경된다() {
        // given: 시작일 = 오늘
        createTripWithPeriod(TripStatus.RECRUITMENT_CLOSED, LocalDate.now(), LocalDate.now().plusDays(3));

        // when
        scheduler.updateTripStatuses();

        // then
        Trip result = tripRepository.findAll().get(0);
        assertThat(result.getStatus()).isEqualTo(TripStatus.IN_PROGRESS);
    }

    @Test
    void RECRUITING_여행도_시작일이_지나면_IN_PROGRESS로_변경된다() {
        // given: 시작일 = 어제
        createTripWithPeriod(TripStatus.RECRUITING, LocalDate.now().minusDays(1), LocalDate.now().plusDays(2));

        // when
        scheduler.updateTripStatuses();

        // then
        Trip result = tripRepository.findAll().get(0);
        assertThat(result.getStatus()).isEqualTo(TripStatus.IN_PROGRESS);
    }

    @Test
    void IN_PROGRESS_여행이_종료일_다음날_COMPLETED로_변경된다() {
        // given: 종료일 = 어제
        createTripWithPeriod(TripStatus.IN_PROGRESS, LocalDate.now().minusDays(3), LocalDate.now().minusDays(1));

        // when
        scheduler.updateTripStatuses();

        // then
        Trip result = tripRepository.findAll().get(0);
        assertThat(result.getStatus()).isEqualTo(TripStatus.COMPLETED);
    }

    @Test
    void 시작일이_아직_안된_여행은_IN_PROGRESS로_변경되지_않는다() {
        // given: 시작일 = 내일
        createTripWithPeriod(TripStatus.RECRUITMENT_CLOSED, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));

        // when
        scheduler.updateTripStatuses();

        // then
        Trip result = tripRepository.findAll().get(0);
        assertThat(result.getStatus()).isEqualTo(TripStatus.RECRUITMENT_CLOSED);
    }

    @Test
    void 종료일이_아직_지나지_않은_여행은_COMPLETED로_변경되지_않는다() {
        // given: 종료일 = 오늘 (당일은 아직 여행중)
        createTripWithPeriod(TripStatus.IN_PROGRESS, LocalDate.now().minusDays(2), LocalDate.now());

        // when
        scheduler.updateTripStatuses();

        // then
        Trip result = tripRepository.findAll().get(0);
        assertThat(result.getStatus()).isEqualTo(TripStatus.IN_PROGRESS);
    }

    @Test
    void 이미_COMPLETED인_여행은_다시_처리되지_않는다() {
        // given
        createTripWithPeriod(TripStatus.COMPLETED, LocalDate.now().minusDays(5), LocalDate.now().minusDays(1));

        // when
        scheduler.updateTripStatuses();

        // then
        Trip result = tripRepository.findAll().get(0);
        assertThat(result.getStatus()).isEqualTo(TripStatus.COMPLETED);
    }

    @Test
    void 여러_여행을_한번에_일괄_처리한다() {
        // given
        createTripWithPeriod(TripStatus.RECRUITMENT_CLOSED, LocalDate.now(), LocalDate.now().plusDays(3));
        createTripWithPeriod(TripStatus.RECRUITMENT_CLOSED, LocalDate.now(), LocalDate.now().plusDays(5));
        createTripWithPeriod(TripStatus.IN_PROGRESS, LocalDate.now().minusDays(3), LocalDate.now().minusDays(1));
        createTripWithPeriod(TripStatus.RECRUITMENT_CLOSED, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)); // 아직 안 된 것

        // when
        scheduler.updateTripStatuses();

        // then
        long inProgressCount = tripRepository.findAll().stream()
                .filter(t -> t.getStatus() == TripStatus.IN_PROGRESS).count();
        long completedCount = tripRepository.findAll().stream()
                .filter(t -> t.getStatus() == TripStatus.COMPLETED).count();
        long recruitmentClosedCount = tripRepository.findAll().stream()
                .filter(t -> t.getStatus() == TripStatus.RECRUITMENT_CLOSED).count();

        assertThat(inProgressCount).isEqualTo(2);
        assertThat(completedCount).isEqualTo(1);
        assertThat(recruitmentClosedCount).isEqualTo(1);
    }
}
