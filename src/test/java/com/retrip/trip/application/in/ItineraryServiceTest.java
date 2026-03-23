package com.retrip.trip.application.in;

import com.retrip.trip.application.in.base.BaseItineraryServiceTest;
import com.retrip.trip.application.in.request.ItineraryDetailMoveRequest;
import com.retrip.trip.application.in.request.ItineraryDetailReorderRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsBulkCreateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsUpdateRequest;
import com.retrip.trip.application.in.request.ItineraryRequestFixture;
import com.retrip.trip.application.in.response.ItineraryDetailsCreateResponse;
import com.retrip.trip.application.in.response.ItineraryDetailsUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.ItineraryDetail;
import com.retrip.trip.domain.entity.Trip;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ItineraryServiceTest extends BaseItineraryServiceTest {

    @Test
    @DisplayName("여행의 상세 일정을 생성 한다.")
    void createItineraryDetails() {
        //given
        Trip saveTrip = tripRepository.save(trip);
        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();

        //when
        ItineraryDetailsCreateResponse response = itineraryService.createItineraryDetails(
                saveTrip.getId(), itinerary.getId(), ItineraryRequestFixture.createItineraryDetails(locationId));

        //then
        assertThat(response.locationId()).isEqualTo(locationId);
        assertThat(response.time()).isNull();
        assertThat(response.memo()).isNull();
        assertThat(response.sortOrder()).isEqualTo(0);
    }

    @Test
    @DisplayName("세부일정 시간과 메모를 수정 한다.")
    void updateItineraryDetails() {
        //given
        Trip saveTrip = tripRepository.save(trip);
        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();

        ItineraryDetailsCreateResponse createResponse = itineraryService.createItineraryDetails(
                saveTrip.getId(), itinerary.getId(), ItineraryRequestFixture.createItineraryDetails(locationId));

        LocalDateTime time = itinerary.getDate().atTime(LocalTime.now()).plusHours(2);
        ItineraryDetailsUpdateRequest updateRequest = ItineraryRequestFixture.updateItineraryDetails(time, "속초 관람차", null);

        //when
        ItineraryDetailsUpdateResponse response = itineraryService.updateItineraryDetails(
                saveTrip.getId(), itinerary.getId(), createResponse.id(), updateRequest);

        //then
        assertThat(response.memo()).isEqualTo("속초 관람차");
        assertThat(response.time()).isEqualTo(time.truncatedTo(ChronoUnit.HOURS));
    }

    @Test
    @DisplayName("여행의 일정 상세를 제거 한다.")
    void deleteItineraryDetails() {
        //given
        Trip saveTrip = tripRepository.save(trip);
        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();

        //when
        ItineraryDetailsCreateResponse createResponse = itineraryService.createItineraryDetails(
                saveTrip.getId(), itinerary.getId(), ItineraryRequestFixture.createItineraryDetails(locationId));

        //then
        assertDoesNotThrow(() -> itineraryService.deleteItineraryDetail(
                saveTrip.getId(), itinerary.getId(), createResponse.id()));
    }

    @Test
    @DisplayName("세부일정의 순서를 변경 한다.")
    void reorderItineraryDetails() {
        //given
        Trip saveTrip = tripRepository.save(trip);
        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();

        ItineraryDetailsCreateResponse first = itineraryService.createItineraryDetails(
                saveTrip.getId(), itinerary.getId(), ItineraryRequestFixture.createItineraryDetails(locationId));
        ItineraryDetailsCreateResponse second = itineraryService.createItineraryDetails(
                saveTrip.getId(), itinerary.getId(), ItineraryRequestFixture.createItineraryDetails(locationId));

        //when
        itineraryService.reorderItineraryDetails(saveTrip.getId(), itinerary.getId(),
                new ItineraryDetailReorderRequest(List.of(second.id(), first.id())));

        //then
        Itinerary reloaded = tripItineraryQueryRepository.findByIdWithItineraryDetails(itinerary.getId()).orElseThrow();
        assertThat(reloaded.getItineraryDetails().getValues().stream()
                .filter(d -> d.getId().equals(second.id()))
                .findFirst().orElseThrow().getSortOrder()).isEqualTo(0);
        assertThat(reloaded.getItineraryDetails().getValues().stream()
                .filter(d -> d.getId().equals(first.id()))
                .findFirst().orElseThrow().getSortOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("세부일정을 다른 날짜 맨 뒤로 이동한다.")
    void moveItineraryDetailToLast() {
        //given
        Trip saveTrip = tripRepository.save(trip);
        Itinerary firstItinerary = saveTrip.getItineraries().getValues().getFirst();
        Itinerary lastItinerary = saveTrip.getItineraries().getValues().getLast();

        // target에 미리 2개 추가
        itineraryService.createItineraryDetails(saveTrip.getId(), lastItinerary.getId(),
                ItineraryRequestFixture.createItineraryDetails(locationId)); // sortOrder 0
        itineraryService.createItineraryDetails(saveTrip.getId(), lastItinerary.getId(),
                ItineraryRequestFixture.createItineraryDetails(locationId)); // sortOrder 1

        ItineraryDetailsCreateResponse toMove = itineraryService.createItineraryDetails(
                saveTrip.getId(), firstItinerary.getId(), ItineraryRequestFixture.createItineraryDetails(locationId));

        //when
        // target 맨 뒤(sortOrder=2)로 이동
        itineraryService.moveItineraryDetail(saveTrip.getId(), firstItinerary.getId(), toMove.id(),
                new ItineraryDetailMoveRequest(lastItinerary.getId(), 2));

        //then
        Itinerary reloadedSource = tripItineraryQueryRepository.findByIdWithItineraryDetails(firstItinerary.getId()).orElseThrow();
        Itinerary reloadedTarget = tripItineraryQueryRepository.findByIdWithItineraryDetails(lastItinerary.getId()).orElseThrow();

        assertThat(reloadedSource.getItineraryDetails().getValues()).isEmpty();
        assertThat(reloadedTarget.getItineraryDetails().getValues()).hasSize(3);

        List<ItineraryDetail> sorted = reloadedTarget.getItineraryDetails().getValues().stream()
                .sorted(Comparator.comparingInt(ItineraryDetail::getSortOrder))
                .toList();
        assertThat(sorted.getLast().getId()).isEqualTo(toMove.id());
        assertThat(sorted.getLast().getSortOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("세부일정을 다른 날짜 중간에 끼워 넣는다.")
    void moveItineraryDetailToMiddle() {
        //given
        Trip saveTrip = tripRepository.save(trip);
        Itinerary firstItinerary = saveTrip.getItineraries().getValues().getFirst();
        Itinerary lastItinerary = saveTrip.getItineraries().getValues().getLast();

        // target에 A(0), B(1), C(2) 추가
        ItineraryDetailsCreateResponse detailA = itineraryService.createItineraryDetails(
                saveTrip.getId(), lastItinerary.getId(), ItineraryRequestFixture.createItineraryDetails(locationId));
        ItineraryDetailsCreateResponse detailB = itineraryService.createItineraryDetails(
                saveTrip.getId(), lastItinerary.getId(), ItineraryRequestFixture.createItineraryDetails(locationId));
        ItineraryDetailsCreateResponse detailC = itineraryService.createItineraryDetails(
                saveTrip.getId(), lastItinerary.getId(), ItineraryRequestFixture.createItineraryDetails(locationId));

        ItineraryDetailsCreateResponse toMove = itineraryService.createItineraryDetails(
                saveTrip.getId(), firstItinerary.getId(), ItineraryRequestFixture.createItineraryDetails(locationId));

        //when
        // B(1) 위치에 끼워 넣기 → 결과: A(0), toMove(1), B(2), C(3)
        itineraryService.moveItineraryDetail(saveTrip.getId(), firstItinerary.getId(), toMove.id(),
                new ItineraryDetailMoveRequest(lastItinerary.getId(), 1));

        //then
        Itinerary reloadedTarget = tripItineraryQueryRepository.findByIdWithItineraryDetails(lastItinerary.getId()).orElseThrow();
        List<ItineraryDetail> sorted = reloadedTarget.getItineraryDetails().getValues().stream()
                .sorted(Comparator.comparingInt(ItineraryDetail::getSortOrder))
                .toList();

        assertThat(sorted).hasSize(4);
        assertThat(sorted.get(0).getId()).isEqualTo(detailA.id());
        assertThat(sorted.get(1).getId()).isEqualTo(toMove.id());
        assertThat(sorted.get(2).getId()).isEqualTo(detailB.id());
        assertThat(sorted.get(3).getId()).isEqualTo(detailC.id());
    }

    @Test
    @DisplayName("하루 세부일정 전체를 일괄 생성하며 sortOrder가 순서대로 부여된다.")
    void bulkCreateItineraryDetails() {
        //given
        Trip saveTrip = tripRepository.save(trip);
        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();

        ItineraryDetailsBulkCreateRequest request = ItineraryRequestFixture.bulkCreateItineraryDetails(List.of(
                ItineraryRequestFixture.bulkItem(locationId, "센소지", null),
                ItineraryRequestFixture.bulkItem(locationId, "웨스트 긴자", null),
                ItineraryRequestFixture.bulkItem(locationId, "도쿄 타워", null)
        ));

        //when
        List<ItineraryDetailsCreateResponse> responses = itineraryService.bulkCreateItineraryDetails(
                saveTrip.getId(), itinerary.getId(), request);

        //then
        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).sortOrder()).isEqualTo(0);
        assertThat(responses.get(1).sortOrder()).isEqualTo(1);
        assertThat(responses.get(2).sortOrder()).isEqualTo(2);
        assertThat(responses.get(0).memo()).isEqualTo("센소지");
        assertThat(responses.get(2).memo()).isEqualTo("도쿄 타워");
    }

    @Test
    @DisplayName("기존 세부일정이 있을 때 일괄 생성 시 sortOrder가 기존 마지막 이후로 이어진다.")
    void bulkCreateItineraryDetailsAppendsAfterExisting() {
        //given
        Trip saveTrip = tripRepository.save(trip);
        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();

        itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(),
                ItineraryRequestFixture.createItineraryDetails(locationId)); // sortOrder 0

        ItineraryDetailsBulkCreateRequest request = ItineraryRequestFixture.bulkCreateItineraryDetails(List.of(
                ItineraryRequestFixture.bulkItem(locationId, "복구항목A", null),
                ItineraryRequestFixture.bulkItem(locationId, "복구항목B", null)
        ));

        //when
        List<ItineraryDetailsCreateResponse> responses = itineraryService.bulkCreateItineraryDetails(
                saveTrip.getId(), itinerary.getId(), request);

        //then
        assertThat(responses.get(0).sortOrder()).isEqualTo(1);
        assertThat(responses.get(1).sortOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("하루 세부일정을 전체 삭제하면 세부일정이 모두 제거되고 일자는 유지된다.")
    void deleteAllItineraryDetails() {
        //given
        Trip saveTrip = tripRepository.save(trip);
        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();

        itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(),
                ItineraryRequestFixture.createItineraryDetails(locationId));
        itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(),
                ItineraryRequestFixture.createItineraryDetails(locationId));

        //when
        itineraryService.deleteAllItineraryDetails(saveTrip.getId(), itinerary.getId());

        //then
        Itinerary reloaded = tripItineraryQueryRepository.findByIdWithItineraryDetails(itinerary.getId()).orElseThrow();
        assertThat(reloaded.getItineraryDetails().getValues()).isEmpty();
    }

    @Test
    @DisplayName("하루 일정 삭제 후 일괄 생성으로 복구하면 원래 순서가 유지된다.")
    void deleteAllAndBulkCreateRestoresOriginalOrder() {
        //given
        Trip saveTrip = tripRepository.save(trip);
        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();

        List<ItineraryDetailsCreateResponse> original = List.of(
                itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(),
                        ItineraryRequestFixture.createItineraryDetails(locationId)),
                itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(),
                        ItineraryRequestFixture.createItineraryDetails(locationId)),
                itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(),
                        ItineraryRequestFixture.createItineraryDetails(locationId))
        );

        itineraryService.deleteAllItineraryDetails(saveTrip.getId(), itinerary.getId());

        // 프론트가 메모리에 보관해둔 데이터로 복구 요청
        ItineraryDetailsBulkCreateRequest restoreRequest = ItineraryRequestFixture.bulkCreateItineraryDetails(
                original.stream()
                        .map(r -> ItineraryRequestFixture.bulkItem(r.locationId(), r.memo(), r.time()))
                        .toList()
        );

        //when
        List<ItineraryDetailsCreateResponse> restored = itineraryService.bulkCreateItineraryDetails(
                saveTrip.getId(), itinerary.getId(), restoreRequest);

        //then
        assertThat(restored).hasSize(3);
        assertThat(restored.get(0).sortOrder()).isEqualTo(0);
        assertThat(restored.get(1).sortOrder()).isEqualTo(1);
        assertThat(restored.get(2).sortOrder()).isEqualTo(2);
        assertThat(restored.stream().map(ItineraryDetailsCreateResponse::locationId).toList())
                .containsExactlyElementsOf(original.stream().map(ItineraryDetailsCreateResponse::locationId).toList());
    }

    @Test
    @DisplayName("여행 일정을 조회한다.")
    void getItineraries() {
        //given
        Trip saveTrip = tripRepository.save(trip);
        Itinerary firstItinerary = saveTrip.getItineraries().getValues().getFirst();
        Itinerary lastItinerary = saveTrip.getItineraries().getValues().getLast();

        itineraryService.createItineraryDetails(saveTrip.getId(), firstItinerary.getId(),
                ItineraryRequestFixture.createItineraryDetails(locationId));
        itineraryService.createItineraryDetails(saveTrip.getId(), firstItinerary.getId(),
                ItineraryRequestFixture.createItineraryDetails(locationId));
        itineraryService.createItineraryDetails(saveTrip.getId(), lastItinerary.getId(),
                ItineraryRequestFixture.createItineraryDetails(locationId));

        //when
        List<ItineraryResponse> result = itineraryService.getItineraries(saveTrip.getId());

        //then
        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().itineraryDetails()).hasSize(2);
    }
}