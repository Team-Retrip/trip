package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.fixture.TripFixture;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripPeriod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItineraryDetailsTest {
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");
    Trip trip;
    Itinerary itinerary;

    @BeforeEach
    void setUp() {
        trip = TripFixture.createTestTripWithPeriod(memberId, "속초 여행 맴버 구함", "속초 여행은 이렇게이렇게 갈겁니다~",
                TripCategory.DOMESTIC, new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)));
        itinerary = trip.getItineraries().getValues().getFirst();
    }

    @DisplayName("세부일정 추가 시 sortOrder가 순서대로 부여된다.")
    @Test
    void addItineraryDetailAssignsSortOrder() {
        ItineraryDetail detail1 = ItineraryDetail.create(null, null, itinerary, locationId, 0);
        ItineraryDetail detail2 = ItineraryDetail.create(null, null, itinerary, locationId, 1);
        itinerary.addItineraryDetail(detail1);
        itinerary.addItineraryDetail(detail2);

        assertThat(itinerary.getItineraryDetails().getValues()).hasSize(2);
        assertThat(detail1.getSortOrder()).isEqualTo(0);
        assertThat(detail2.getSortOrder()).isEqualTo(1);
    }

    @DisplayName("세부일정 삭제 후 남은 항목의 sortOrder가 재정렬된다.")
    @Test
    void removeItineraryDetailReordersSortOrder() {
        ItineraryDetail detail1 = ItineraryDetail.create(null, null, itinerary, locationId, 0);
        ItineraryDetail detail2 = ItineraryDetail.create(null, null, itinerary, locationId, 1);
        ItineraryDetail detail3 = ItineraryDetail.create(null, null, itinerary, locationId, 2);
        itinerary.addItineraryDetail(detail1);
        itinerary.addItineraryDetail(detail2);
        itinerary.addItineraryDetail(detail3);

        itinerary.removeItineraryDetail(detail1.getId());

        List<ItineraryDetail> remaining = itinerary.getItineraryDetails().getValues();
        assertThat(remaining).hasSize(2);
        assertThat(remaining.stream().map(ItineraryDetail::getSortOrder).sorted().toList())
                .containsExactly(0, 1);
    }

    @DisplayName("시간 없이도 세부일정을 추가할 수 있다.")
    @Test
    void addItineraryDetailWithoutTime() {
        ItineraryDetail detail = ItineraryDetail.create(null, null, itinerary, locationId, 0);
        itinerary.addItineraryDetail(detail);

        assertThat(itinerary.getItineraryDetails().getValues()).hasSize(1);
        assertThat(itinerary.getItineraryDetails().getValues().getFirst().getTimeValue()).isNull();
    }

    @DisplayName("다른 날짜의 시간을 가진 세부일정도 추가할 수 있다.")
    @Test
    void addItineraryDetailFromDifferentDate() {
        ItineraryDetail detail = ItineraryDetail.create(null, LocalDateTime.now(), itinerary, locationId, 0);
        itinerary.addItineraryDetail(detail);

        assertThat(itinerary.getItineraryDetails().getValues()).hasSize(1);
    }

    @DisplayName("세부일정이 없는 상태에서 일괄 추가 시 sortOrder가 0부터 순서대로 부여된다.")
    @Test
    void addAllAssignsSortOrderFromZeroWhenEmpty() {
        List<ItineraryDetail> details = List.of(
                ItineraryDetail.create("메모1", null, itinerary, locationId, 0),
                ItineraryDetail.create("메모2", null, itinerary, locationId, 0),
                ItineraryDetail.create("메모3", null, itinerary, locationId, 0)
        );

        itinerary.addItineraryDetails(details);

        List<ItineraryDetail> result = itinerary.getItineraryDetails().getValues().stream()
                .sorted(Comparator.comparingInt(ItineraryDetail::getSortOrder))
                .toList();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getSortOrder()).isEqualTo(0);
        assertThat(result.get(1).getSortOrder()).isEqualTo(1);
        assertThat(result.get(2).getSortOrder()).isEqualTo(2);
    }

    @DisplayName("기존 세부일정이 있을 때 일괄 추가 시 sortOrder가 기존 마지막 순서 이후로 이어진다.")
    @Test
    void addAllAppendsSortOrderAfterExistingDetails() {
        itinerary.addItineraryDetail(ItineraryDetail.create(null, null, itinerary, locationId, 0)); // sortOrder 0
        itinerary.addItineraryDetail(ItineraryDetail.create(null, null, itinerary, locationId, 1)); // sortOrder 1

        List<ItineraryDetail> newDetails = List.of(
                ItineraryDetail.create("복구A", null, itinerary, locationId, 0),
                ItineraryDetail.create("복구B", null, itinerary, locationId, 0)
        );

        itinerary.addItineraryDetails(newDetails);

        List<ItineraryDetail> result = itinerary.getItineraryDetails().getValues().stream()
                .sorted(Comparator.comparingInt(ItineraryDetail::getSortOrder))
                .toList();
        assertThat(result).hasSize(4);
        assertThat(newDetails.get(0).getSortOrder()).isEqualTo(2);
        assertThat(newDetails.get(1).getSortOrder()).isEqualTo(3);
    }
}