package com.retrip.trip.domain.service;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.TripInvitationDuplicateException;
import com.retrip.trip.domain.exception.common.IllegalStateException;
import com.retrip.trip.domain.vo.TripStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static com.retrip.trip.domain.fixture.TripFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;

class InvitationPolicyTest {
    InvitationPolicy invitationPolicy = new InvitationPolicy();

    @Test
    void 리더가_아닌_멤버가_사용자를_초대하면_예외가_발생한다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        TripParticipant participant = TripParticipant.createTripParticipant(혁진_ID, trip);
        trip.addParticipant(participant);
        List<UUID> memberIds = List.of(정수_ID, 홍석_ID, 준호_ID);

        // when, then
        assertThatThrownBy(() -> invitationPolicy.canInvite(trip, 혁진_ID, memberIds))
                .isExactlyInstanceOf(MemberIsNotLeaderException.class);
    }

    @Test
    void 이미_여행_멤버인_사용자를_초대하면_예외가_발생한다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        trip.addParticipant(TripParticipant.createTripParticipant(혁진_ID, trip));
        trip.addParticipant(TripParticipant.createTripParticipant(지수_ID, trip));
        List<UUID> memberIds = List.of(혁진_ID);

        // when, then
        assertThatThrownBy(() -> invitationPolicy.canInvite(trip, LEADER_ID, memberIds))
                .isExactlyInstanceOf(TripInvitationDuplicateException.class);
    }

    @ParameterizedTest
    @EnumSource(mode = INCLUDE, names = {
            "BEFORE_TRIP",
            "IN_PROGRESS",
            "COMPLETED"
    })
    void 여행이_모집중이거나_모집완료인_경우에만_초대를_생성할_수_있다(TripStatus status) {
        // given
        Trip trip = createTrip(TRIP_ID);
        ReflectionTestUtils.setField(trip, "status", status);
        List<UUID> memberIds = List.of(혁진_ID);

        // when, then
        assertThatThrownBy(() -> invitationPolicy.canInvite(trip, LEADER_ID, memberIds))
                .isExactlyInstanceOf(IllegalStateException.class);
    }
}
