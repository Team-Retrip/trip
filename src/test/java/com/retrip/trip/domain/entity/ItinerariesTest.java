package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ItinerariesTest {
    UUID memberId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c003");
    UUID participantId = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c004");
    UUID locationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");
    UUID destinationId = UUID.fromString("13c8ab91-76bc-4f70-93e9-89f1a65dc64a");
    LocalDate start = LocalDate.now().plusDays(1);
    LocalDate end = start.plusDays(10);
    UUID 속초_해수욕장_Id = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c004");
    UUID 속초_중앙_시장_Id = UUID.fromString("c076d246-7e6d-4191-bf5c-310aebf4c006");

    @DisplayName("여행 기간의 일자 만큼 일정 목록을 생성 한다.")
    @Test
    void ofPeriod() {
        TripPeriod period = new TripPeriod(start, end);
        Trip trip = Trip.createWithItineraries(
                memberId,
                UUID.randomUUID(),
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                period,
                true,
                4,
                TripCategory.DOMESTIC
        );
        Itineraries itineraries = new Itineraries(trip, period);
        assertThat(itineraries.getValues().size()).isEqualTo(11);
    }

    @DisplayName("일정의 날짜가 기간을 벗어나면 예외가 발생한다.")
    @Test
    void out_of_period() {
        TripPeriod period = new TripPeriod(start, end);
        List<LocalDate> dates = List.of(end.plusDays(1), start);

        Trip trip = Trip.createWithItineraries(
                memberId,
                UUID.randomUUID(),
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                period,
                true,
                4,
                TripCategory.DOMESTIC
        );
        List<Itinerary> itineraries = dates.stream().map(d -> Itinerary.create(trip, d)).toList();


        assertThatThrownBy(() -> new Itineraries(period, itineraries))
                .isExactlyInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("날짜 목록으로 일정을 생성한다.")
    @Test
    void irregular() {
        TripPeriod period = new TripPeriod(start, end);
        List<LocalDate> dates = List.of(start, start.plusDays(1), start.plusDays(2));

        Trip trip = Trip.createWithItineraries(
                memberId,
                UUID.randomUUID(),
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                period,
                true,
                4,
                TripCategory.DOMESTIC
        );
        List<Itinerary> itineraries = dates.stream().map(d -> Itinerary.create(trip, d)).toList();

        Itineraries result = new Itineraries(period, itineraries);
        assertThat(result.getValues().size()).isEqualTo(3);
        assertThat(result.getValues().getFirst().getName()).isEqualTo("day 1");
    }

    @DisplayName("리더는 여행 기간을 수정할 수 있다.")
    @Test
    void updateItineraryByLeader() {
        TripPeriod updatePeriod = new TripPeriod(start.plusDays(2), end.plusDays(1));
        Trip trip = Trip.createWithItineraries(
                memberId,
                destinationId,
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(start, end),
                true,
                4,
                TripCategory.DOMESTIC
        );
        List<Itinerary> itineraries = List.of(
                Itinerary.create(trip, start.plusDays(2)),
                Itinerary.create(trip, start.plusDays(3)),
                Itinerary.create(trip, start.plusDays(5)));
        itineraries.getFirst().createItineraryDetails(
                List.of(
                        ItineraryDetail.create(10000L, "해수욕장에서 사진 및 카페 사진 찍기", itineraries.getFirst(), 속초_해수욕장_Id),
                        ItineraryDetail.create(50000L, "속초 중앙 시장에서 오징어 순대, 닭강정 먹기", itineraries.getFirst(), 속초_중앙_시장_Id),
                        ItineraryDetail.create(null, "숙소에서 쉬기", itineraries.getFirst(), null)));
        itineraries.getLast().createItineraryDetails(
                List.of(
                        ItineraryDetail.create(0L, "집가기", itineraries.getLast(), null)
                ));


        assertThatCode(() -> trip.updateItineraries(itineraries, memberId)).doesNotThrowAnyException();
    }

    @DisplayName("사용자는 여행 일자와 여행 기간을 수정할 수 없다.")
    @Test
    void updateItineraryByParticipant() {
        TripPeriod updatePeriod = new TripPeriod(start.plusDays(2), end.plusDays(1));
        Trip trip = Trip.createWithItineraries(
                memberId,
                destinationId,
                new TripTitle("속초 여행 멤버 구함"),
                new TripDescription("속초 여행은 이렇게이렇게 갈겁니다~"),
                new TripPeriod(start, end),
                true,
                4,
                TripCategory.DOMESTIC
        );
        List<Itinerary> itineraries = List.of(
                Itinerary.create(trip, start.plusDays(2)),
                Itinerary.create(trip, start.plusDays(3)),
                Itinerary.create(trip, start.plusDays(5)));
        itineraries.getFirst().createItineraryDetails(
                List.of(
                        ItineraryDetail.create(10000L, "해수욕장에서 사진 및 카페 사진 찍기", itineraries.getFirst(), 속초_해수욕장_Id),
                        ItineraryDetail.create(50000L, "속초 중앙 시장에서 오징어 순대, 닭강정 먹기", itineraries.getFirst(), 속초_중앙_시장_Id),
                        ItineraryDetail.create(null, "숙소에서 쉬기", itineraries.getFirst(), null)));
        itineraries.getLast().createItineraryDetails(
                List.of(
                        ItineraryDetail.create(0L, "집가기", itineraries.getLast(), null)
                ));
        //todo: 추후 참여자 로직 생성시, 변경 필요
        TripParticipant participant = TripParticipant.createTripParticipant(participantId, trip);
        trip.getParticipants().getValues().add(participant);


        assertThatThrownBy(() -> trip.updateItineraries(itineraries, participantId
        )).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
