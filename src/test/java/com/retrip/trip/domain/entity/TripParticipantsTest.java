package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.TripFullException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
import org.junit.jupiter.api.Test;

import static com.retrip.trip.domain.fixture.TripFixture.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

class TripParticipantsTest {

    @Test
    public void 리더만_업데이트를_할_수_있다() {
        //given
        Trip trip = createTrip(TRIP_ID);
        TripParticipants tripParticipants = new TripParticipants(LEADER_ID, trip, 4);

        //when
        boolean result = tripParticipants.updatableByLeader(LEADER_ID);

        //then
        assertTrue(result);
    }

    @Test
    public void 여행에_포함된_맴버가_아니라면_업데이트를_할_수_없다() {
        //given
        Trip trip = createTrip(TRIP_ID);
        TripParticipants tripParticipants = new TripParticipants(LEADER_ID, trip, 4);

        //when, then
        assertThrows(InvalidValueException.class, () -> tripParticipants.updatableByLeader(MEMBER_ID));

    }

    @Test
    public void 리더가_아니라면_업데이트를_할_수_없다() {
        //given
        Trip trip = createTrip(TRIP_ID);
        TripParticipants tripParticipants = new TripParticipants(LEADER_ID, trip, 4);
        tripParticipants.addParticipant(TripParticipant.createTripParticipant(MEMBER_ID, trip));

        //when
        boolean result = tripParticipants.updatableByLeader(MEMBER_ID);

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
            tripParticipants.updateMaxParticipants(1,userId);
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
        tripParticipants.updateMaxParticipants(5,userId);

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
