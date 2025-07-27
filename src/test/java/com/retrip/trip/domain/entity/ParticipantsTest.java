package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.entity.participant.Participant;
import com.retrip.trip.domain.entity.participant.Participants;
import com.retrip.trip.domain.exception.ParticipantFullException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.fixture.ParticipantFixture;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.retrip.trip.domain.fixture.TripFixture.*;
import static org.junit.jupiter.api.Assertions.*;

class ParticipantsTest {

    @Test
    public void 리더가_아니라면_업데이트를_할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participants participants = new Participants(LEADER_ID, trip, 4);
        participants.addParticipant(Participant.createTripParticipant(MEMBER_ID, trip));

        // when
        boolean result = participants.updatableByLeader(MEMBER_ID);

        // then
        assertFalse(result);
    }

    @Test
    void 최대_참여_인원보다_많은_인원이_참여할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participants participants = new Participants(LEADER_ID, trip, 2);
        UUID member1 = UUID.fromString("33333333-3333-3333-3333-333333333333");

        // when
        participants.addParticipant(Participant.createTripParticipant(member1, trip));

        // then
        assertThrows(
                ParticipantFullException.class,
                () -> {
                    UUID member2 = UUID.fromString("44444444-4444-4444-4444-444444444444");
                    participants.addParticipant(Participant.createTripParticipant(member2, trip));
                });
    }

    @Test
    void 최대_참여_인원을_현재_참여_인원보다_적게_변경할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participants participants = new Participants(LEADER_ID, trip, 3);
        UUID member1 = UUID.fromString("33333333-3333-3333-3333-333333333333");
        participants.addParticipant(Participant.createTripParticipant(member1, trip));

        // then
        assertThrows(
                InvalidValueException.class,
                () -> participants.updateMaxParticipants(1, LEADER_ID));
    }

    @Test
    void 최대_참여_인원은_1명_이상이어야_한다() {
        // given
        Trip trip = createTrip(TRIP_ID);

        // then
        assertThrows(InvalidValueException.class, () -> new Participants(LEADER_ID, trip, 0));
    }

    @Test
    void 리더가_최대_참여_인원을_변경할_수_있다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participants participants = new Participants(LEADER_ID, trip, 3);

        // when
        participants.updateMaxParticipants(5, LEADER_ID);

        // then
        assertEquals(5, trip.getMaxParticipants());
    }

    @Test
    void 현재_참여_인원수를_정확히_반환한다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participants participants = new Participants(LEADER_ID, trip, 3);
        UUID member1 = UUID.fromString("33333333-3333-3333-3333-333333333333");
        participants.addParticipant(Participant.createTripParticipant(member1, trip));

        // when
        int currentCount = participants.getCurrentCount();

        // then
        assertEquals(2, currentCount); // 리더 + 참여자 1명
    }
}
