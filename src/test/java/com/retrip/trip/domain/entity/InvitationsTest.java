package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.entity.invitation.Invitations;
import com.retrip.trip.domain.exception.TripInvitationDuplicateException;
import com.retrip.trip.domain.vo.InvitationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static com.retrip.trip.domain.fixture.TripFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;

class InvitationsTest {
    @Test
    void 여행_초대를_추가한다() {
        Invitations invitations = new Invitations(new ArrayList<>());
        invitations.add(TRIP_ID, List.of(정수_ID, 홍석_ID, 준호_ID));
        assertThat(invitations.getValues().size()).isEqualTo(3);
    }

    @Test
    void 사용자를_여행에_중복_초대하면_예외가_발생한다() {
        Invitations invitations = new Invitations(List.of(
                new Invitation(TRIP_ID, 정수_ID),
                new Invitation(TRIP_ID, 홍석_ID),
                new Invitation(TRIP_ID, 준호_ID)
        ));
        assertThatThrownBy(() -> invitations.add(TRIP_ID, List.of(홍석_ID)))
                .isExactlyInstanceOf(TripInvitationDuplicateException.class);
    }

    @ParameterizedTest
    @EnumSource(mode = INCLUDE, names = {"REJECTED", "EXPIRED"})
    void 거절되거나_만료된_초대는_다시_요청할_수_있다(InvitationStatus status) {
        // given
        Invitations invitations = new Invitations(List.of(
                new Invitation(TRIP_ID, 홍석_ID),
                new Invitation(TRIP_ID, 준호_ID)
        ));
        invitations.getValues()
                .forEach(i -> ReflectionTestUtils.setField(i, "status", status));

        // when
        invitations.add(TRIP_ID, List.of(홍석_ID));
        invitations.add(TRIP_ID, List.of(준호_ID));

        // then
        invitations.getValues().forEach(
                i -> assertThat(i.getStatus()).isEqualTo(InvitationStatus.INVITED)
        );
    }

    @Test
    void 수락한_초대는_다시_요청할_수_없다() {
        // given
        Invitations invitations = new Invitations(List.of(
                new Invitation(TRIP_ID, 홍석_ID),
                new Invitation(TRIP_ID, 준호_ID)
        ));
        invitations.getValues()
                .forEach(i -> ReflectionTestUtils.setField(i, "status", InvitationStatus.ACCEPTED));

        // when, then
        assertThatThrownBy(() -> invitations.add(TRIP_ID, List.of(홍석_ID)))
                .isExactlyInstanceOf(TripInvitationDuplicateException.class);
    }
}
