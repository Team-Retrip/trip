package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.TripInvitationDuplicateException;
import com.retrip.trip.domain.exception.common.IllegalStateException;
import com.retrip.trip.domain.vo.TripInvitationStatus;
import com.retrip.trip.domain.vo.TripStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.retrip.trip.domain.fixture.TripFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;

class TripInvitationsTest {
    @Test
    void 여행_초대를_추가한다() {
        Trip trip = createTrip(TRIP_ID);
        TripInvitations invitations = new TripInvitations();
        invitations.add(trip, LEADER_ID, List.of(정수_ID, 홍석_ID, 준호_ID), trip.getTripParticipants());
        assertThat(invitations.getValues().size()).isEqualTo(3);
    }

    @Test
    void 사용자를_여행에_중복_초대하면_예외가_발생한다() {
        Trip trip = createTrip(TRIP_ID);
        TripInvitations invitations = new TripInvitations();
        invitations.add(trip, LEADER_ID, List.of(정수_ID, 홍석_ID, 준호_ID), trip.getTripParticipants());
        assertThatThrownBy(() -> invitations.add(trip, LEADER_ID, List.of(홍석_ID), trip.getTripParticipants()))
                .isExactlyInstanceOf(TripInvitationDuplicateException.class);
    }

    @Test
    void 리더가_아닌_멤버가_사용자를_초대하면_예외가_발생한다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        TripParticipant participant = TripParticipant.createTripParticipant(혁진_ID, trip);
        trip.addParticipant(participant);

        TripInvitations invitations = new TripInvitations();
        invitations.add(trip, LEADER_ID, List.of(정수_ID, 홍석_ID, 준호_ID), trip.getTripParticipants());

        // when, then
        assertThatThrownBy(() -> invitations.add(trip, 혁진_ID, List.of(지수_ID), trip.getTripParticipants()))
                .isExactlyInstanceOf(MemberIsNotLeaderException.class);
    }

    @ParameterizedTest
    @EnumSource(mode = INCLUDE, names = {"REJECTED", "EXPIRED"})
    void 거절되거나_만료된_초대는_다시_요청할_수_있다(TripInvitationStatus status) {
        // given
        Trip trip = createTrip(TRIP_ID);
        TripInvitations invitations = new TripInvitations();
        invitations.add(trip, LEADER_ID, List.of(정수_ID, 홍석_ID), trip.getTripParticipants());
        invitations.getValues()
                .forEach(i -> ReflectionTestUtils.setField(i, "status", status));

        // when
        invitations.add(trip, LEADER_ID, List.of(정수_ID, 홍석_ID), trip.getTripParticipants());

        // then
        invitations.getValues().forEach(
                i -> assertThat(i.getStatus()).isEqualTo(TripInvitationStatus.INVITED)
        );
    }

    @Test
    void 수락한_초대는_다시_요청할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        TripInvitations invitations = new TripInvitations();
        invitations.add(trip, LEADER_ID, List.of(정수_ID, 홍석_ID), trip.getTripParticipants());
        invitations.getValues()
                .forEach(i -> ReflectionTestUtils.setField(i, "status", TripInvitationStatus.ACCEPTED));

        // when, then
        assertThatThrownBy(() -> invitations.add(trip, LEADER_ID, List.of(정수_ID, 홍석_ID), trip.getTripParticipants()))
                .isExactlyInstanceOf(TripInvitationDuplicateException.class);
    }

    @Test
    void 이미_여행_멤버인_사용자를_초대하면_예외가_발생한다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        trip.addParticipant(TripParticipant.createTripParticipant(혁진_ID, trip));
        trip.addParticipant(TripParticipant.createTripParticipant(지수_ID, trip));
        TripInvitations invitations = new TripInvitations();

        // when, then
        assertThatThrownBy(() -> invitations.add(trip, LEADER_ID, List.of(지수_ID), trip.getTripParticipants()))
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
        TripInvitations invitations = new TripInvitations();

        // when, then
        assertThatThrownBy(() -> invitations.add(trip, LEADER_ID, List.of(정수_ID, 홍석_ID, 준호_ID), trip.getTripParticipants()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }
}
