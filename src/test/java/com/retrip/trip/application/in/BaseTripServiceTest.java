package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripFixture;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BaseTripServiceTest extends com.retrip.trip.application.in.base.BaseTripServiceTest {
    @DisplayName("여행을 생성 한다.")
    @Test
    void createTrip() {
        TripCreateRequest request =
                TripFixture.createRequest(
                        memberId,
                        locationId,
                        "속초 여행 멤버 구함",
                        "속초 여행은 이렇게이렇게 갈겁니다~",
                        start,
                        end,
                        true,
                        4,
                        TripCategory.DOMESTIC);
        TripCreateResponse response = tripService.createTrip(request);
        assertThat(response.id()).isNotNull();
        assertThat(response.destinationId()).isEqualTo(locationId);
    }

    @DisplayName("여행 목록을 조회한다.")
    @Test
    void getTrips() {
        TripPeriod period = new TripPeriod(start, end);
        tripRepository.save(
                Trip.create(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("속초 여행 멤버 구함"),
                        new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));
        tripRepository.save(
                Trip.create(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("강릉 여행 멤버 구함"),
                        new TripDescription("강릉 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));
        tripRepository.save(
                Trip.create(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("대구 여행 멤버 구함"),
                        new TripDescription("대구 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));
        tripRepository.save(
                Trip.create(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("부산 여행 멤버 구함"),
                        new TripDescription("부산 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC));

        Page<TripResponse> trips = tripService.getTrips(PageRequest.of(0, 2));

        assertThat(trips.getTotalElements()).isEqualTo(2);
        assertThat(trips.getPageable().getOffset()).isEqualTo(0);
        assertThat(trips.getPageable().getPageSize()).isEqualTo(2);
    }

    @DisplayName("여행 일정을 수정한다.")
    @Test
    void updatePeriod() {
        TripPeriod period = new TripPeriod(start, end);
        Trip trip =
                tripRepository.save(
                        Trip.create(
                                memberId,
                                UUID.randomUUID(),
                                new TripTitle("속초 여행 멤버 구함"),
                                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                                period,
                                true,
                                4,
                                TripCategory.DOMESTIC));

        TripPeriod updatePeriod = new TripPeriod(start.plusDays(3), end.plusDays(2));
        trip.updatePeriod(updatePeriod, memberId);

        assertThat(trip.getId()).isNotNull();
        assertThat(trip.getPeriod().getStart()).isEqualTo(updatePeriod.getStart());
        assertThat(trip.getPeriod().getEnd()).isEqualTo(updatePeriod.getEnd());
    }
}
