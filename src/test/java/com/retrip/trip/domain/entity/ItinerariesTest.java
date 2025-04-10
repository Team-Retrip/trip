package com.retrip.trip.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

class ItinerariesTest {

    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID participantId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c004");
    UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");
    UUID destinationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");
    LocalDate start = LocalDate.now().plusDays(1);
    LocalDate end = start.plusDays(10);
    UUID 속초_해수욕장_Id = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c004");
    UUID 속초_중앙_시장_Id = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c006");

    @DisplayName("일정의 날짜가 기간을 벗어나면 예외가 발생한다.")
    @Test
    void out_of_period() {
        TripPeriod period = new TripPeriod(start, end);
        List<LocalDate> dates = List.of(end.plusDays(1), start);

        Trip trip =
                Trip.create(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("속초 여행 멤버 구함"),
                        new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC);
        List<Itinerary> itineraries = dates.stream().map(d -> Itinerary.create(trip, d)).toList();

        assertThatThrownBy(() -> new Itineraries(period, itineraries))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("날짜 목록으로 일정을 생성한다.")
    @Test
    void irregular() {
        TripPeriod period = new TripPeriod(start, end);
        List<LocalDate> dates = List.of(start, start.plusDays(1), start.plusDays(2));

        Trip trip =
                Trip.create(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("속초 여행 멤버 구함"),
                        new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC);
        List<Itinerary> itineraries = dates.stream().map(d -> Itinerary.create(trip, d)).toList();

        Itineraries result = new Itineraries(period, itineraries);
        assertThat(result.getValues().size()).isEqualTo(3);
        assertThat(result.getValues().getFirst().getName()).isEqualTo("day 1");
    }
}
