package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItineraryServiceTest {
    @Autowired
    TripRepository tripRepository;

    ItineraryService itineraryService;
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID 속초_해수욕장_Id = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c004");
    UUID 속초_중앙_시장_Id = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c006");
    LocalDate start = LocalDate.now().plusDays(1);
    LocalDate end = start.plusDays(10);

    @BeforeEach
    void setUp() {
        itineraryService = new ItineraryService(tripRepository);
    }

    @DisplayName("여행의 일정 목록을 생성 한다.")
    @Test
    void createItineraries() {
        TripPeriod period = new TripPeriod(start, end);
        Trip trip = tripRepository.save(Trip.createWithItineraries(memberId, UUID.randomUUID(), new TripTitle("속초 여행 멤버 구함"), new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"), period, true, 4, TripCategory.DOMESTIC));
        List<ItinerariesCreateRequest.ItineraryCreateRequest> itineraries = List.of(
                new ItinerariesCreateRequest.ItineraryCreateRequest(start.plusDays(1), null),
                new ItinerariesCreateRequest.ItineraryCreateRequest(
                        start.plusDays(2),
                        List.of(
                                new ItinerariesCreateRequest.ItineraryCreateRequest.ItineraryDetailCreateRequest(10000L, 속초_해수욕장_Id, "해수욕장에서 사진 및 카페 사진 찍기"),
                                new ItinerariesCreateRequest.ItineraryCreateRequest.ItineraryDetailCreateRequest(50000L, 속초_중앙_시장_Id, "속초 중앙 시장에서 오징어 순대, 닭강정 먹기")
                        )
                ),
                new ItinerariesCreateRequest.ItineraryCreateRequest(start.plusDays(3), null)
        );

        ItinerariesCreateRequest request = new ItinerariesCreateRequest(trip.getId(), itineraries);

        ItinerariesCreateResponse response = itineraryService.createItineraries(request);
        assertThat(response.tripId()).isNotNull();
        assertThat(response.itineraries().size()).isEqualTo(3);
    }

    @DisplayName("여행의 일정 목록을 수정 한다.")
    @Test
    void updateItineraries() {
        TripPeriod period = new TripPeriod(
                start,
                end
        );
        Trip trip = tripRepository.save(
                Trip.createWithItineraries(
                        memberId,
                        UUID.randomUUID(),
                        new TripTitle("속초 여행 멤버 구함"),
                        new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                        period,
                        true,
                        4,
                        TripCategory.DOMESTIC
                ));
        List<ItinerariesUpdateRequest.ItineraryUpdateRequest> itineraries = List.of(
                new ItinerariesUpdateRequest.ItineraryUpdateRequest(start.plusDays(2), null),
                new ItinerariesUpdateRequest.ItineraryUpdateRequest(start.plusDays(3), null),
                new ItinerariesUpdateRequest.ItineraryUpdateRequest(
                        start.plusDays(4),
                        List.of(
                                new ItinerariesUpdateRequest.ItineraryUpdateRequest.ItineraryDetailUpdateRequest(
                                        10000L, 속초_해수욕장_Id, "해수욕장에서 사진 및 카페 사진 찍기"
                                ),
                                new ItinerariesUpdateRequest.ItineraryUpdateRequest.ItineraryDetailUpdateRequest(
                                        50000L, 속초_중앙_시장_Id, "속초 중앙 시장에서 오징어 순대, 닭강정 먹기"
                                )
                        )
                ),
                new ItinerariesUpdateRequest.ItineraryUpdateRequest(start.plusDays(5), null)
        );

        ItinerariesUpdateRequest request = new ItinerariesUpdateRequest(trip.getId(), memberId, itineraries);

        ItinerariesUpdateResponse response = itineraryService.updateItineraries(request);
        assertThat(response.tripId()).isNotNull();
        assertThat(response.itineraries().size()).isEqualTo(4);
        assertThat(response.itineraries().get(2).itineraryDetails().size()).isEqualTo(2);
    }
}
