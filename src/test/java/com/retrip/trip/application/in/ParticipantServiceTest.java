package com.retrip.trip.application.in;

import static com.retrip.trip.domain.fixture.TripFixture.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.retrip.trip.application.in.base.BaseInvitationServiceTest;
import com.retrip.trip.application.in.service.ParticipantService;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.participant.Participant;
import com.retrip.trip.domain.exception.NotLeaderException;
import com.retrip.trip.domain.fixture.ParticipantFixture;
import com.retrip.trip.domain.vo.ParticipantRole;
import com.retrip.trip.domain.vo.ParticipantStatus;

import jakarta.transaction.Transactional;
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
    void 참여자를_제거한다() {
        // given
        Participant response = participantService.createParticipant(TRIP_ID, MEMBER_ID, 4);

        // when

        // then
        assertDoesNotThrow(() -> participantService.remove(TRIP_ID, MEMBER_ID));
    }

    @Test
    void 내가_참여중인_여행을_볼_수_있다() {
        // given
        participantService.createParticipant(TRIP_ID, MEMBER_ID, 10);
        participantService.createParticipant(TRIP_ID, MEMBER_ID, 10);
        participantService.createParticipant(TRIP_ID, MEMBER_ID, 10);
        participantService.createParticipant(TRIP_ID, MEMBER_ID, 10);

        // when
        List<Participant> response =
                participantService.findByMemberId(MEMBER_ID, PageRequest.of(0, 10));

        // then
        assertThat(response.size()).isEqualTo(4);
    }

    @Test
    void 내가_참여중인_여행을_총_갯수를_볼_수_있다() {
        participantService.createParticipant(TRIP_ID, MEMBER_ID, 10);
        participantService.createParticipant(TRIP_ID, MEMBER_ID, 10);
        participantService.createParticipant(TRIP_ID, MEMBER_ID, 10);
        participantService.createParticipant(TRIP_ID, MEMBER_ID, 10);

        // when
        Long response = participantService.findByMemberIdTotalCount(MEMBER_ID);

        // then
        assertThat(response).isEqualTo(4);
    }

    @Test
    public void 리더는_정상_동작() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant leader = ParticipantFixture.createLeaderParticipant(TRIP_ID, LEADER_ID);

        // when
        // then
        assertDoesNotThrow(
                () ->
                        participantService.requireLeaderOrElseThrow(
                                trip.getId(), leader.getMemberId()));
    }

    @Test
    public void 리더가_아니면_오류_발생() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant participant = ParticipantFixture.createParticipant(TRIP_ID, MEMBER_ID);

        // when
        // then
        assertThrows(
                NotLeaderException.class,
                () ->
                        participantService.requireLeaderOrElseThrow(
                                trip.getId(), participant.getMemberId()));
    }

    @Test
    public void 참여자는_정상_동작() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant participant = ParticipantFixture.createParticipant(TRIP_ID, LEADER_ID);

        // when
        // then
        assertDoesNotThrow(
                () ->
                        participantService.requireParticipantOrElseThrow(
                                trip.getId(), participant.getMemberId()));
    }

    @Test
    public void 참여자가_아니면_오류_발생() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant leader = ParticipantFixture.createLeaderParticipant(TRIP_ID, MEMBER_ID);

        // when
        // then
        assertThrows(
                NotLeaderException.class,
                () ->
                        participantService.requireParticipantOrElseThrow(
                                trip.getId(), leader.getMemberId()));
    }

    @Test
    public void 리더를_위임_한다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant leader = ParticipantFixture.createLeaderParticipant(TRIP_ID, LEADER_ID);
        Participant participant = ParticipantFixture.createParticipant(TRIP_ID, LEADER_ID);

        // when
        Participant response =
                participantService.delegateLeader(
                        TRIP_ID, leader.getMemberId(), participant.getMemberId());

        // then
        assertThat(response.getRole()).isEqualTo(ParticipantRole.LEADER);
        assertThat(leader.getRole()).isEqualTo(ParticipantRole.PARTICIPANT);
    }

    @Test
    public void 방장은_여행에_참여하는_인원을_수락할_수_있다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant leader = ParticipantFixture.createLeaderParticipant(TRIP_ID, LEADER_ID);

        // when
        ;

        // then
        assertDoesNotThrow(
                () ->
                        participantService.canInvite(
                                TRIP_ID, leader.getMemberId(), List.of(홍석_ID, 정수_ID)));
    }
}
