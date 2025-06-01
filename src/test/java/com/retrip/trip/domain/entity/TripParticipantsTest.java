package com.retrip.trip.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.vo.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

class TripParticipantsTest {
    UUID userId = UUID.fromString("11111111-2222-3333-4444-555555555555");
    UUID tripId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private Trip createDummyTrip() {
        return Trip.builder()
                .id(tripId)
                .destinationId(UUID.randomUUID())
                .title(new TripTitle("테스트 여행"))
                .description(new TripDescription("테스트 설명"))
                .period(new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)))
                .open(true)
                .maxParticipants(4)
                .status(TripStatus.RECRUITING)
                .category(TripCategory.DOMESTIC)
                .build();
    }

    @Test
    public void 리더만_업데이트를_할_수_있다() {
        //given
        Trip dummyTrip = createDummyTrip();
        TripParticipants tripParticipants = new TripParticipants(userId, dummyTrip);

        //when
        boolean result = tripParticipants.updatableByLeader(userId);

        //then
        assertTrue(result);
    }

    @Test
    public void 여행에_포함된_맴버가_아니라면_업데이트를_할_수_없다() {
        //given
        Trip dummyTrip = createDummyTrip();
        TripParticipants tripParticipants = new TripParticipants(userId, dummyTrip);
        UUID member1 = UUID.fromString("33333333-3333-3333-3333-333333333333");
        //when

        //then
        assertThrows(InvalidValueException.class, () -> tripParticipants.updatableByLeader(member1));

    }

    @Test
    public void 리더가_아니라면_업데이트를_할_수_없다() {
        //given
        Trip dummyTrip = createDummyTrip();
        TripParticipants tripParticipants = new TripParticipants(userId, dummyTrip);
        UUID member1 = UUID.fromString("33333333-3333-3333-3333-333333333333");
        tripParticipants.addParticipant(TripParticipant.createTripParticipant(member1, dummyTrip));

        //when
        boolean result = tripParticipants.updatableByLeader(member1);

        //then
        assertFalse(result);
    }
}
