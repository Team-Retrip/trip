package com.retrip.trip.application.in;

import static org.assertj.core.api.Assertions.assertThat;

import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsUpdateRequest;
import com.retrip.trip.application.in.request.ItineraryFixture;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryDetailDeleteResponse;
import com.retrip.trip.application.in.response.ItineraryDetailsUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

class BaseItineraryServiceTest
        extends com.retrip.trip.application.in.base.BaseItineraryServiceTest {

    @Test
    @DisplayName("여행의 일정 목록을 생성한다.")
    void createItineraries() {
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
        ItinerariesUpdateRequest itineraries =
                ItineraryFixture.updateItineraryRequest(
                        List.of(start.plusDays(2), start.plusDays(3), end.minusDays(1)));

        ItinerariesUpdateResponse response =
                itineraryService.updateItineraries(trip.getId(), itineraries);

        assertThat(response.itineraries().size()).isEqualTo(3);
        assertThat(response.itineraries().getFirst().date()).isEqualTo(start.plusDays(2));
        assertThat(response.itineraries().getLast().date()).isEqualTo(end.minusDays(1));
    }

    @Test
    @DisplayName("여행의 일정 목록을 수정한다.")
    void updateItineraries() {
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

        ItinerariesUpdateRequest createItineraries =
                ItineraryFixture.updateItineraryRequest(
                        List.of(start.plusDays(2), start.plusDays(3), end.minusDays(1)));

        itineraryService.updateItineraries(trip.getId(), createItineraries);

        ItinerariesUpdateRequest updateItineraries =
                ItineraryFixture.updateItineraryRequest(
                        List.of(start.plusDays(2), start.plusDays(3)));

        ItinerariesUpdateResponse response =
                itineraryService.updateItineraries(trip.getId(), updateItineraries);

        assertThat(response.itineraries().size()).isEqualTo(2);
        assertThat(response.itineraries().getFirst().date()).isEqualTo(start.plusDays(2));
        assertThat(response.itineraries().getLast().date()).isEqualTo(start.plusDays(3));
    }

    @Test
    @DisplayName("여행의 상세 일정을 등록한다.")
    void createItineraryDetails() {
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

        ItinerariesUpdateRequest createItineraries =
                ItineraryFixture.updateItineraryRequest(
                        List.of(start.plusDays(2), start.plusDays(3), end.minusDays(1)));

        ItinerariesUpdateResponse itineraries =
                itineraryService.updateItineraries(trip.getId(), createItineraries);

        ItineraryDetailsUpdateRequest itineraryDetailsUpdateRequest =
                ItineraryFixture.updateItineraryDetailsRequest(
                        List.of(
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        속초_해수욕장_Id, 10_000L, "속초 해수욕장"),
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        속초_중앙_시장_Id, 50_000L, "속초 중앙시장 투어"),
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        숙소_Id, null, "숙소 복귀")));
        UUID itineraryId = itineraries.itineraries().getFirst().id();
        ItineraryDetailsUpdateResponse response =
                itineraryService.updateItineraryDetails(
                        trip.getId(), itineraryId, itineraryDetailsUpdateRequest);

        assertThat(response.itineraryId()).isEqualTo(itineraryId);
        assertThat(response.itineraryDetails().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("여행의 상세 일정을 등록한다.")
    void updateItineraryDetails() {
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

        ItinerariesUpdateRequest createItineraries =
                ItineraryFixture.updateItineraryRequest(
                        List.of(start.plusDays(2), start.plusDays(3), end.minusDays(1)));

        ItinerariesUpdateResponse itineraries =
                itineraryService.updateItineraries(trip.getId(), createItineraries);

        ItineraryDetailsUpdateRequest itineraryDetailsCreateRequest =
                ItineraryFixture.updateItineraryDetailsRequest(
                        List.of(
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        속초_해수욕장_Id, 10_000L, "속초 해수욕장"),
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        속초_중앙_시장_Id, 50_000L, "속초 중앙시장 투어"),
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        숙소_Id, null, "숙소 복귀")));

        UUID itineraryId = itineraries.itineraries().getFirst().id();

        itineraryService.updateItineraryDetails(
                trip.getId(), itineraryId, itineraryDetailsCreateRequest);

        ItineraryDetailsUpdateRequest itineraryDetailsUpdateRequest =
                ItineraryFixture.updateItineraryDetailsRequest(
                        List.of(
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        속초_해수욕장_Id, 20_000L, "속초 해수욕장"),
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        집_Id, null, "비와서 집 복귀")));

        ItineraryDetailsUpdateResponse response =
                itineraryService.updateItineraryDetails(
                        trip.getId(), itineraryId, itineraryDetailsUpdateRequest);

        assertThat(response.itineraryId()).isEqualTo(itineraryId);
        assertThat(response.itineraryDetails().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("여행의 상세 일정을 삭제한다.")
    void deleteItineraryDetail() {
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

        ItinerariesUpdateRequest createItineraries =
                ItineraryFixture.updateItineraryRequest(
                        List.of(start.plusDays(2), start.plusDays(3), end.minusDays(1)));

        ItinerariesUpdateResponse itineraries =
                itineraryService.updateItineraries(trip.getId(), createItineraries);

        ItineraryDetailsUpdateRequest itineraryDetailsUpdateRequest =
                ItineraryFixture.updateItineraryDetailsRequest(
                        List.of(
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        속초_해수욕장_Id, 10_000L, "속초 해수욕장"),
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        속초_중앙_시장_Id, 50_000L, "속초 중앙시장 투어"),
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        숙소_Id, null, "숙소 복귀")));
        UUID itineraryId = itineraries.itineraries().getFirst().id();
        ItineraryDetailsUpdateResponse itineraryDetailsUpdateResponse =
                itineraryService.updateItineraryDetails(
                        trip.getId(), itineraryId, itineraryDetailsUpdateRequest);
        UUID itineraryDetailId = itineraryDetailsUpdateResponse.itineraryDetails().getFirst().id();

        ItineraryDetailDeleteResponse response =
                itineraryService.deleteItineraryDetail(
                        trip.getId(), itineraryId, itineraryDetailId);

        assertThat(response.id()).isEqualTo(itineraryDetailId);
    }

    @DisplayName("여행 일정을 조회한다.")
    @Test
    void getItineraries() {
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
        trip.updateItineraries(List.of(start, start.plusDays(1), end.minusDays(1)));

        ItinerariesUpdateRequest createItineraries =
                ItineraryFixture.updateItineraryRequest(
                        List.of(start.plusDays(2), start.plusDays(3), end.minusDays(1)));

        ItinerariesUpdateResponse itineraries =
                itineraryService.updateItineraries(trip.getId(), createItineraries);

        ItineraryDetailsUpdateRequest itineraryDetailsUpdateRequest =
                ItineraryFixture.updateItineraryDetailsRequest(
                        List.of(
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        속초_해수욕장_Id, 10_000L, "속초 해수욕장"),
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        속초_중앙_시장_Id, 50_000L, "속초 중앙시장 투어"),
                                new ItineraryDetailsUpdateRequest.ItineraryDetailUpdateRequest(
                                        숙소_Id, null, "숙소 복귀")));

        UUID itineraryId = itineraries.itineraries().getFirst().id();
        itineraryService.updateItineraryDetails(
                trip.getId(), itineraryId, itineraryDetailsUpdateRequest);
        Page<ItineraryResponse> searchItineraryResponses =
                itineraryService.getItineraries(trip.getId(), PageRequest.of(0, 3));

        assertThat(searchItineraryResponses.getTotalElements()).isEqualTo(3L);
        assertThat(searchItineraryResponses.getPageable().getOffset()).isEqualTo(0);
        assertThat(searchItineraryResponses.getPageable().getPageSize()).isEqualTo(3);
    }
}
