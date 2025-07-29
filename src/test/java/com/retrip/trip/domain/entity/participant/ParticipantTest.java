package com.retrip.trip.domain.entity.participant;

import static com.retrip.trip.domain.fixture.TripFixture.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.retrip.trip.domain.fixture.ParticipantFixture;
import com.retrip.trip.domain.vo.ParticipantRole;
import com.retrip.trip.domain.vo.ParticipantStatus;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ParticipantTest {
    @Test
    void 여행_참여를_금지한다() {
        Participant participant = ParticipantFixture.createParticipant(TRIP_ID, MEMBER_ID);
        participant.ban();
        assertThat(participant.getStatus()).isEqualTo(ParticipantStatus.EXPELLED);
    }

    @Test
    void 사용자의_권한을_변경한다() {
        Participant participant = ParticipantFixture.createParticipant(TRIP_ID, MEMBER_ID);
        participant.changeRole(ParticipantRole.PARTICIPANT);
        assertThat(participant.getRole().getViewName())
                .isEqualTo(ParticipantRole.PARTICIPANT.getViewName());
    }
}
