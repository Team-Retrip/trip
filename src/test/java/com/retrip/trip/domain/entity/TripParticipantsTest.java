package com.retrip.trip.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.retrip.trip.domain.exception.TripFullException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.vo.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

class TripParticipantsTest {
    UUID userId = UUID.fromString("11111111-2222-3333-4444-555555555555");
    UUID tripId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private Trip createDummyTrip() {
        return Trip.create(
                userId,
                tripId,
                new TripTitle("테스트 여행"),
                new TripDescription("테스트 설명"),
                new TripPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)),
                true,
                4,
                TripCategory.DOMESTIC
        );
    }

    @Test
    public void 리더만_업데이트를_할_수_있다() {
        //given
        Trip dummyTrip = createDummyTrip();
        TripParticipants tripParticipants = new TripParticipants(userId, dummyTrip,4);

        //when
        boolean result = tripParticipants.updatableByLeader(userId);

        //then
        assertTrue(result);
    }

    @Test
    public void 여행에_포함된_맴버가_아니라면_업데이트를_할_수_없다() {
        //given
        Trip dummyTrip = createDummyTrip();
        TripParticipants tripParticipants = new TripParticipants(userId, dummyTrip,4);
        UUID member1 = UUID.fromString("33333333-3333-3333-3333-333333333333");
        //when

        //then
        assertThrows(InvalidValueException.class, () -> tripParticipants.updatableByLeader(member1));

    }

    @Test
    public void 리더가_아니라면_업데이트를_할_수_없다() {
        //given
        Trip dummyTrip = createDummyTrip();
        TripParticipants tripParticipants = new TripParticipants(userId, dummyTrip,4);
        UUID member1 = UUID.fromString("33333333-3333-3333-3333-333333333333");
        tripParticipants.addParticipant(TripParticipant.createTripParticipant(member1, dummyTrip));

        //when
        boolean result = tripParticipants.updatableByLeader(member1);

        //then
        assertFalse(result);
    }
    @Test
    void 최대_참여_인원보다_많은_인원이_참여할_수_없다() {
        // given
        Trip dummyTrip = createDummyTrip();
        TripParticipants tripParticipants = new TripParticipants(userId, dummyTrip, 2);
        UUID member1 = UUID.fromString("33333333-3333-3333-3333-333333333333");

        // when
        tripParticipants.addParticipant(TripParticipant.createTripParticipant(member1, dummyTrip));

        // then
        assertThrows(TripFullException.class, () -> {
            UUID member2 = UUID.fromString("44444444-4444-4444-4444-444444444444");
            tripParticipants.addParticipant(TripParticipant.createTripParticipant(member2, dummyTrip));
        });
    }

    @Test
    void 최대_참여_인원을_현재_참여_인원보다_적게_변경할_수_없다() {
        // given
        Trip dummyTrip = createDummyTrip();
        TripParticipants tripParticipants = new TripParticipants(userId, dummyTrip, 3);
        UUID member1 = UUID.fromString("33333333-3333-3333-3333-333333333333");
        tripParticipants.addParticipant(TripParticipant.createTripParticipant(member1, dummyTrip));

        // then
        assertThrows(InvalidValueException.class, () -> {
            tripParticipants.updateMaxParticipants(1);
        });
    }

    @Test
    void 최대_참여_인원은_1명_이상이어야_한다() {
        // given
        Trip dummyTrip = createDummyTrip();

        // then
        assertThrows(InvalidValueException.class, () -> {
            new TripParticipants(userId, dummyTrip, 0);
        });
    }

    @Test
    void 리더가_최대_참여_인원을_변경할_수_있다() {
        // given
        Trip dummyTrip = createDummyTrip();
        TripParticipants tripParticipants = new TripParticipants(userId, dummyTrip, 3);

        // when
        tripParticipants.updateMaxParticipants(5);

        // then
        assertEquals(5, tripParticipants.getMaxParticipants());
    }

    @Test
    void 현재_참여_인원수를_정확히_반환한다() {
        // given
        Trip dummyTrip = createDummyTrip();
        TripParticipants tripParticipants = new TripParticipants(userId, dummyTrip, 3);
        UUID member1 = UUID.fromString("33333333-3333-3333-3333-333333333333");
        tripParticipants.addParticipant(TripParticipant.createTripParticipant(member1, dummyTrip));

        // when
        int currentCount = tripParticipants.getCurrentCount();

        // then
        assertEquals(2, currentCount); // 리더 + 참여자 1명
    }
}
