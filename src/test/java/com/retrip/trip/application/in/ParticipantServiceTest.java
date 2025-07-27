package com.retrip.trip.application.in;

import static com.retrip.trip.domain.fixture.TripFixture.*;

import static org.assertj.core.api.Assertions.assertThat;

import com.retrip.trip.application.in.base.BaseInvitationServiceTest;
import com.retrip.trip.application.in.service.ParticipantService;

import com.retrip.trip.domain.entity.participant.Participant;
import com.retrip.trip.domain.vo.ParticipantRole;
import com.retrip.trip.domain.vo.ParticipantStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;

class ParticipantServiceTest extends BaseInvitationServiceTest {
    @Autowired private ParticipantService participantService;

    @Test
    void 리더_참여자를_생성한다() {
        // given

        // when
        Participant response = participantService.createLeaderParticipant(TRIP_ID, MEMBER_ID);

        // then
        assertThat(response.getTripId()).isEqualTo(TRIP_ID);
        assertThat(response.getRole()).isEqualTo(ParticipantRole.LEADER);
        assertThat(response.getStatus()).isEqualTo(ParticipantStatus.ACTIVE);
    }

    @Test
    void 내가_참여중인_여행을_볼_수_있다() {
        // given
        participantService.createLeaderParticipant(TRIP_ID, MEMBER_ID);
        participantService.createLeaderParticipant(TRIP_ID, MEMBER_ID);
        participantService.createLeaderParticipant(TRIP_ID, MEMBER_ID);
        participantService.createLeaderParticipant(TRIP_ID, MEMBER_ID);

        // when
        List<Participant> response =
                participantService.findByMemberId(MEMBER_ID, PageRequest.of(0, 10));

        // then
        assertThat(response.size()).isEqualTo(4);
    }

    @Test
    void 내가_참여중인_여행을_총_갯수를_볼_수_있다() {
        // given
        participantService.createLeaderParticipant(TRIP_ID, MEMBER_ID);
        participantService.createLeaderParticipant(TRIP_ID, MEMBER_ID);
        participantService.createLeaderParticipant(TRIP_ID, MEMBER_ID);
        participantService.createLeaderParticipant(TRIP_ID, MEMBER_ID);

        // when
        Long response = participantService.findByMemberIdTotalCount(MEMBER_ID);

        // then
        assertThat(response).isEqualTo(4);
    }
}
