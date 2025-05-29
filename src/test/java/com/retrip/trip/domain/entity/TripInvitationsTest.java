package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.TripInvitationDuplicateException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.retrip.trip.domain.fixture.TripFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TripInvitationsTest {
    @Test
    void 여행_초대를_추가한다() {
        Trip trip = createTrip(TRIP_ID);
        TripInvitations invitations = new TripInvitations();
        invitations.add(trip, LEADER_ID, List.of(정수_ID, 홍석_ID, 준호_ID));
        assertThat(invitations.getValues().size()).isEqualTo(3);
    }

    @Test
    void 사용자를_여행에_중복_초대하면_예외가_발생한다() {
        Trip trip = createTrip(TRIP_ID);
        TripInvitations invitations = new TripInvitations();
        invitations.add(trip, LEADER_ID, List.of(정수_ID, 홍석_ID, 준호_ID));
        assertThatThrownBy(() -> invitations.add(trip, LEADER_ID, List.of(홍석_ID)))
                .isExactlyInstanceOf(TripInvitationDuplicateException.class);
    }

    @Test
    void 리더가_아닌_멤버가_사용자를_초대하면_예외가_발생한다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        TripParticipant participant = TripParticipant.createTripParticipant(혁진_ID, trip);
        trip.addParticipant(participant);

        TripInvitations invitations = new TripInvitations();
        invitations.add(trip, LEADER_ID, List.of(정수_ID, 홍석_ID, 준호_ID));

        // when, then
        assertThatThrownBy(() -> invitations.add(trip, 혁진_ID, List.of(지수_ID)))
                .isExactlyInstanceOf(MemberIsNotLeaderException.class);
    }
}
