package com.retrip.trip.application.in;

import static org.assertj.core.api.Assertions.assertThat;

import com.retrip.trip.application.in.base.BaseItineraryServiceTest;
import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;
import com.retrip.trip.domain.entity.Trip;
import java.time.LocalDate;
import java.util.List;
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
}
