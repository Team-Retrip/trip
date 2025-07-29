package com.retrip.trip.domain.service;

import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_MEMBER_BANNED_CANNOT_APPLY;
import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_MEMBER_NOT_IN_TRIP;
import static com.retrip.trip.domain.fixture.TripFixture.*;
import static com.retrip.trip.domain.fixture.TripFixture.TRIP_ID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.participant.Participant;
import com.retrip.trip.domain.exception.NotLeaderException;
import com.retrip.trip.domain.exception.NotParticipantException;
import com.retrip.trip.domain.exception.ParticipantFullException;
import com.retrip.trip.domain.exception.TripInvitationDuplicateException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.fixture.ParticipantFixture;

import com.retrip.trip.domain.vo.ParticipantRole;

import java.util.List;
import java.util.UUID;

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
    void 리더가_아니면_오류_발생() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant participant = ParticipantFixture.createParticipant(trip.getId(), MEMBER_ID);

        // when, then
        assertThatThrownBy(() -> participantPolicy.validateLeader(participant.getRole()))
                .isExactlyInstanceOf(NotLeaderException.class);
    }

    @Test
    void 참여자가_아니면_오류_발생() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant participant =
                ParticipantFixture.createLeaderParticipant(trip.getId(), MEMBER_ID);

        // when, then
        assertThatThrownBy(() -> participantPolicy.validateParticipant(participant.getRole()))
                .isExactlyInstanceOf(NotParticipantException.class);
    }

    @Test
    void 자기_자신에게_리더를_위임할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant leader = ParticipantFixture.createLeaderParticipant(trip.getId(), MEMBER_ID);

        // when, then
        assertThatThrownBy(() -> participantPolicy.validateDelegate(leader, leader))
                .isExactlyInstanceOf(InvalidValueException.class);
    }

    @Test
    void 리더가_아니면_위임할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant participant = ParticipantFixture.createParticipant(trip.getId(), MEMBER_ID);
        Participant leader =
                ParticipantFixture.createLeaderParticipant(trip.getId(), UUID.randomUUID());

        // when, then
        assertThatThrownBy(() -> participantPolicy.validateDelegate(participant, leader))
                .isExactlyInstanceOf(NotLeaderException.class);
    }

    @Test
    void 리더를_위임할_수_있다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant participant = ParticipantFixture.createParticipant(trip.getId(), MEMBER_ID);
        Participant leader =
                ParticipantFixture.createLeaderParticipant(trip.getId(), UUID.randomUUID());

        // when, then
        participantPolicy.changeLeader(leader, participant);
        assertThat(leader.getRole().getViewName())
                .isEqualTo(ParticipantRole.PARTICIPANT.getViewName());
        assertThat(participant.getRole().getViewName())
                .isEqualTo(ParticipantRole.LEADER.getViewName());
    }

    @Test
    void 이미_여행_멤버인_사용자를_초대하면_예외가_발생한다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant leader = ParticipantFixture.createLeaderParticipant(TRIP_ID, MEMBER_ID);
        Participant participant1 = ParticipantFixture.createParticipant(TRIP_ID, 혁진_ID);
        Participant participant2 = ParticipantFixture.createParticipant(TRIP_ID, 지수_ID);

        List<UUID> memberIds = List.of(혁진_ID);

        // when, then
        assertThatThrownBy(
                        () ->
                                participantPolicy.validateInvite(
                                        List.of(leader, participant1, participant2), memberIds))
                .isExactlyInstanceOf(TripInvitationDuplicateException.class);
    }

    @Test
    void 여행에_참여하지_않은_사용자를_참여_금지할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant leader = ParticipantFixture.createLeaderParticipant(trip.getId(), MEMBER_ID);
        Participant participant =
                ParticipantFixture.createParticipant(UUID.randomUUID(), UUID.randomUUID());

        // when, then
        assertThatThrownBy(
                        () ->
                                participantPolicy.validateBan(
                                        List.of(leader), List.of(participant.getMemberId())))
                .isExactlyInstanceOf(BusinessException.class);
    }

    @Test
    void 금지상태인_사용자는_여행_요청을_할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Participant leader = ParticipantFixture.createLeaderParticipant(trip.getId(), MEMBER_ID);
        Participant participant =
                ParticipantFixture.createParticipant(trip.getId(), UUID.randomUUID());
        participant.ban();

        // when, then
        assertThatThrownBy(() -> participantPolicy.validateDemand(participant))
                .isExactlyInstanceOf(BusinessException.class);
    }
}
