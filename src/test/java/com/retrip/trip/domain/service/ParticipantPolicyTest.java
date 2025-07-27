package com.retrip.trip.domain.service;

import static com.retrip.trip.domain.fixture.TripFixture.*;
import static com.retrip.trip.domain.fixture.TripFixture.TRIP_ID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.participant.Participant;
import com.retrip.trip.domain.exception.NotLeaderException;
import com.retrip.trip.domain.exception.ParticipantFullException;
import com.retrip.trip.domain.fixture.ParticipantFixture;

import org.junit.jupiter.api.Test;

class ParticipantPolicyTest {
    ParticipantPolicy participantPolicy = new ParticipantPolicy();

    @Test
    void 여행_최대인원보다_많이_참여할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        ;
        // when, then
        assertThatThrownBy(() -> participantPolicy.validate(trip.getMaxParticipants(), 4L))
                .isExactlyInstanceOf(ParticipantFullException.class);
    }

    @Test
    void 리더가_아니면_업데이트를_할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant participant = ParticipantFixture.createParticipant(trip.getId(), MEMBER_ID);

        // when, then
        assertThatThrownBy(() -> participantPolicy.validateLeader(participant.getRole()))
                .isExactlyInstanceOf(NotLeaderException.class);
    }
}
