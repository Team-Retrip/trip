package com.retrip.trip.application.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.retrip.trip.application.in.base.BaseItineraryServiceTest;
import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsCreateRequest;
import com.retrip.trip.application.in.request.ItineraryFixture;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;
import com.retrip.trip.application.in.response.ItineraryDetailsCreateResponse;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.ItineraryDetail;
import com.retrip.trip.domain.entity.Trip;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ItineraryServiceTest extends BaseItineraryServiceTest {

    @Test
    @DisplayName("여행의 일정 목록을 생성 한다.")
    void createItineraries() {
        Trip saveTrip = tripRepository.save(trip);

        List<ItinerariesCreateRequest.ItineraryCreateRequest> itineraries =
                List.of(
                        new ItinerariesCreateRequest.ItineraryCreateRequest(LocalDate.now().plusDays(2)),
                        new ItinerariesCreateRequest.ItineraryCreateRequest(LocalDate.now().plusDays(3)),
                        new ItinerariesCreateRequest.ItineraryCreateRequest(LocalDate.now().plusDays(4)));
        ItinerariesCreateRequest request = new ItinerariesCreateRequest(itineraries);
        ItinerariesCreateResponse response =
                itineraryService.createItineraries(saveTrip.getId(), request);
        assertThat(response.tripId()).isNotNull();
        assertThat(response.itineraries().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("여행의 상세 일정을 생성 한다.")
    void createItineraryDetails() {
        Trip saveTrip = tripRepository.save(trip);

        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();
        LocalDateTime time = trip.getItineraries().getValues().getFirst().getDate().atTime(LocalTime.now());
        ItineraryDetailsCreateRequest request = ItineraryFixture.createItineraryDetails(time, "속초 만석 닭강정", 20000L, locationId);

        ItineraryDetailsCreateResponse response = itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(), request);

        assertThat(response.description()).isEqualTo("속초 만석 닭강정");
        assertThat(response.price()).isEqualTo(20000L);
        assertThat(response.time()).isEqualTo(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS));
    }

    @Test
    @DisplayName("여행의 일정 상세를 제거 한다.")
    void deleteItineraryDetails() {
        Trip saveTrip = tripRepository.save(trip);

        Itinerary itinerary = saveTrip.getItineraries().getValues().getFirst();
        LocalDateTime time = trip.getItineraries().getValues().getFirst().getDate().atTime(LocalTime.now());
        ItineraryDetailsCreateRequest createRequest = ItineraryFixture.createItineraryDetails(time, "속초 만석 닭강정", 20000L, locationId);
        ItineraryDetailsCreateResponse response = itineraryService.createItineraryDetails(saveTrip.getId(), itinerary.getId(), createRequest);


        //
        assertDoesNotThrow(() -> itineraryService.deleteItineraryDetail(saveTrip.getId(), itinerary.getId(), response.id()));
    }
}
