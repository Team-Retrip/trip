package com.retrip.trip.application.in;

import com.retrip.trip.application.in.base.BaseItineraryServiceTest;
import com.retrip.trip.application.in.request.ItineraryDetailsCreateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsUpdateRequest;
import com.retrip.trip.application.in.request.ItineraryRequestFixture;
import com.retrip.trip.application.in.response.ItineraryDetailsCreateResponse;
import com.retrip.trip.application.in.response.ItineraryDetailsUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ItineraryServiceTest extends BaseItineraryServiceTest {

    @Test
    @DisplayName("여행의 상세 일정을 생성 한다.")
    void createItineraryDetails() {
        Trip saveTrip = tripRepository.save(trip);

        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();
        LocalDateTime time = trip.getItineraries().getValues().getFirst().getDate().atTime(LocalTime.now());
        ItineraryDetailsCreateRequest request = ItineraryRequestFixture.createItineraryDetails(time, "속초 만석 닭강정", 20000L, locationId);

        ItineraryDetailsCreateResponse response = itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(), request);

        assertThat(response.description()).isEqualTo("속초 만석 닭강정");
        assertThat(response.price()).isEqualTo(20000L);
        assertThat(response.time()).isEqualTo(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS));
    }


    @Test
    @DisplayName("여행의 상세 일정을 수정 한다.")
    void updateItineraryDetails() {
        Trip saveTrip = tripRepository.save(trip);

        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();
        LocalDateTime time1 = trip.getItineraries().getValues().getFirst().getDate().atTime(LocalTime.now());
        LocalDateTime time2 = trip.getItineraries().getValues().getFirst().getDate().atTime(LocalTime.now()).plusHours(1);
        LocalDateTime time3 = trip.getItineraries().getValues().getFirst().getDate().atTime(LocalTime.now()).plusHours(2);
        ItineraryDetailsCreateRequest createRequest1 = ItineraryRequestFixture.createItineraryDetails(time1, "속초 만석 닭강정", 20000L, locationId);
        ItineraryDetailsCreateRequest createRequest2 = ItineraryRequestFixture.createItineraryDetails(time2, "속초 바다", null, locationId);

        ItineraryDetailsUpdateRequest updateRequest = ItineraryRequestFixture.updateItineraryDetails(time3, "속초 관람차", 10000L, locationId);

        ItineraryDetailsCreateResponse createResponse = itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(), createRequest1);
        itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(), createRequest2);

        ItineraryDetailsUpdateResponse response = itineraryService.updateItineraryDetails(saveTrip.getId(), itinerary.getId(), createResponse.id(), updateRequest);

        assertThat(response.description()).isEqualTo("속초 관람차");
        assertThat(response.price()).isEqualTo(10000L);
        assertThat(response.time()).isEqualTo(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS).plusHours(2));
    }

    @Test
    @DisplayName("여행의 중복된 상세 일정으로 수정할 수 없다..")
    void canNotUpdateItineraryDetailsConflicting() {
        Trip saveTrip = tripRepository.save(trip);

        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();
        LocalDateTime time1 = trip.getItineraries().getValues().getFirst().getDate().atTime(LocalTime.now());
        LocalDateTime time2 = trip.getItineraries().getValues().getFirst().getDate().atTime(LocalTime.now()).plusHours(1);
        LocalDateTime time3 = trip.getItineraries().getValues().getFirst().getDate().atTime(LocalTime.now());
        ItineraryDetailsCreateRequest createRequest1 = ItineraryRequestFixture.createItineraryDetails(time1, "속초 만석 닭강정", 20000L, locationId);
        ItineraryDetailsCreateRequest createRequest2 = ItineraryRequestFixture.createItineraryDetails(time2, "속초 바다", null, locationId);
        ItineraryDetailsUpdateRequest updateRequest = ItineraryRequestFixture.updateItineraryDetails(time3, "속초 관람차", 10000L, locationId);

        itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(), createRequest1);
        ItineraryDetailsCreateResponse createResponse = itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(), createRequest2);

        assertThatThrownBy(() -> itineraryService.updateItineraryDetails(saveTrip.getId(), itinerary.getId(), createResponse.id(), updateRequest))
                .isExactlyInstanceOf(InvalidValueException.class);
    }


    @Test
    @DisplayName("여행의 일정 상세를 제거 한다.")
    void deleteItineraryDetails() {
        Trip saveTrip = tripRepository.save(trip);

        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();
        LocalDateTime time = trip.getItineraries().getValues().getFirst().getDate().atTime(LocalTime.now());
        ItineraryDetailsCreateRequest createRequest = ItineraryRequestFixture.createItineraryDetails(time, "속초 만석 닭강정", 20000L, locationId);
        ItineraryDetailsCreateResponse createResponse = itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(), createRequest);

        //
        assertDoesNotThrow(() -> itineraryService.deleteItineraryDetail(saveTrip.getId(), itinerary.getId(), createResponse.id()));
    }

    @DisplayName("여행 일정을 조회한다.")
    @Test
    void getItineraries() {
        Trip saveTrip = tripRepository.save(trip);

        LocalDateTime time1 = trip.getItineraries().getValues().getFirst().getDate().atTime(LocalTime.now());
        LocalDateTime time2 = trip.getItineraries().getValues().getFirst().getDate().atTime(LocalTime.now()).plusHours(1);
        LocalDateTime time3 = trip.getItineraries().getValues().getLast().getDate().atTime(LocalTime.now());
        ItineraryDetailsCreateRequest createRequest1 = ItineraryRequestFixture.createItineraryDetails(time1, "속초 만석 닭강정", 20000L, locationId);
        ItineraryDetailsCreateRequest createRequest2 = ItineraryRequestFixture.createItineraryDetails(time2, "속초 바다", null, locationId);
        ItineraryDetailsCreateRequest createRequest3 = ItineraryRequestFixture.createItineraryDetails(time3, "집가자", null, locationId);

        Itinerary firstItinerary = saveTrip.getItineraries().getValues().getFirst();
        Itinerary lastItinerary = saveTrip.getItineraries().getValues().getLast();

        itineraryService.createItineraryDetails(saveTrip.getId(), firstItinerary.getId(), createRequest2);
        itineraryService.createItineraryDetails(saveTrip.getId(), firstItinerary.getId(), createRequest1);
        itineraryService.createItineraryDetails(saveTrip.getId(), lastItinerary.getId(), createRequest3);

        Page<ItineraryResponse> searchItineraryResponses =
                itineraryService.getItineraries(trip.getId(), PageRequest.of(0, 10));

        assertThat(searchItineraryResponses.getTotalElements()).isEqualTo(10L);
        assertThat(searchItineraryResponses.getPageable().getOffset()).isEqualTo(0);
        assertThat(searchItineraryResponses.getPageable().getPageSize()).isEqualTo(10);
    }
}
