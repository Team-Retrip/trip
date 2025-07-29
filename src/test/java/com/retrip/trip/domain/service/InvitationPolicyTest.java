package com.retrip.trip.domain.service;

import com.retrip.trip.domain.entity.participant.Participant;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.exception.*;
import com.retrip.trip.domain.exception.common.IllegalStateException;
import com.retrip.trip.domain.fixture.ParticipantFixture;
import com.retrip.trip.domain.vo.InvitationStatus;
import com.retrip.trip.domain.vo.ParticipantRole;
import com.retrip.trip.domain.vo.TripStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.retrip.trip.domain.fixture.TripFixture.*;
import static com.retrip.trip.domain.vo.InvitationStatus.EXPIRED;
import static com.retrip.trip.domain.vo.TripStatus.RECRUITING;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;

class InvitationPolicyTest {
    InvitationPolicy invitationPolicy = new InvitationPolicy();

    @ParameterizedTest
    @EnumSource(
            mode = INCLUDE,
            names = {"BEFORE_TRIP", "IN_PROGRESS", "COMPLETED"})
    void 여행이_모집중이거나_모집완료인_경우에만_초대를_생성할_수_있다(TripStatus status) {
        // given
        Trip trip = createTrip(TRIP_ID);
        ReflectionTestUtils.setField(trip, "status", status);
        List<UUID> memberIds = List.of(혁진_ID);

        // when, then
        assertThatThrownBy(() -> invitationPolicy.canInvite(trip))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 초대가_만료되었으면_수락되지_않는다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        ReflectionTestUtils.setField(trip, "status", RECRUITING);
        Invitation invitation = new Invitation(TRIP_ID, MEMBER_ID);
        ReflectionTestUtils.setField(invitation, "status", EXPIRED);

        // when, then
        assertThatThrownBy(() -> invitationPolicy.canAccept(trip, invitation))
                .isExactlyInstanceOf(InvitationExpiredException.class);
    }

    @Test
    void 초대가_만료일이_지났으면_수락되지_않는다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        ReflectionTestUtils.setField(trip, "status", RECRUITING);
        Invitation invitation = new Invitation(TRIP_ID, MEMBER_ID);
        ReflectionTestUtils.setField(invitation, "expiresAt", LocalDateTime.now().minusDays(1));

        // when, then
        assertThatThrownBy(() -> invitationPolicy.canAccept(trip, invitation))
                .isExactlyInstanceOf(InvitationExpiredException.class);
    }

    @ParameterizedTest
    @EnumSource(
            mode = EXCLUDE,
            names = {"RECRUITING"})
    void 여행이_모집중이_아니면_수락되지_않는다(TripStatus status) {
        // given
        Trip trip = createTrip(TRIP_ID);
        ReflectionTestUtils.setField(trip, "status", status);
        Invitation invitation = new Invitation(TRIP_ID, MEMBER_ID);

        // when, then
        assertThatThrownBy(() -> invitationPolicy.canAccept(trip, invitation))
                .isExactlyInstanceOf(TripNotRecruitingException.class);
    }

    @ParameterizedTest
    @EnumSource(
            mode = EXCLUDE,
            names = {"INVITED"})
    void 초대_상태가_아니면_거절할_수_없다(InvitationStatus status) {
        // given
        Invitation invitation = new Invitation(TRIP_ID, MEMBER_ID);
        ReflectionTestUtils.setField(invitation, "status", status);

        // when, then
        assertThatThrownBy(() -> invitationPolicy.canReject(invitation))
                .isExactlyInstanceOf(InvitationRejectNotAllowedException.class);
    }
}
