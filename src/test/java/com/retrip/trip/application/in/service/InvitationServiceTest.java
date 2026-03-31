package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.base.BaseInvitationServiceTest;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.MemberInvitationAcceptResponse;
import com.retrip.trip.application.in.response.MemberInvitationResponse;
import com.retrip.trip.application.in.response.InvitationsCreateResponse;
import com.retrip.trip.application.in.response.InvitationsResponse;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.invitation.Invitation;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static com.retrip.trip.application.in.request.TripInvitationOrder.DATE;
import static com.retrip.trip.domain.fixture.TripFixture.*;
import static com.retrip.trip.domain.vo.InvitationStatus.INVITED;
import static org.assertj.core.api.Assertions.assertThat;

class InvitationServiceTest extends BaseInvitationServiceTest {
    @Test
    void 여행_초대를_생성한다() {
        Trip trip = createTrip(TRIP_ID);
        tripRepository.save(trip);

        TripInvitationsCreateRequest request = new TripInvitationsCreateRequest(List.of(정수_ID, 홍석_ID, 준호_ID));
        InvitationsCreateResponse response = invitationService.createInvitations(TRIP_ID, LEADER_ID, request);
        assertThat(response.tripId()).isEqualTo(TRIP_ID);
        assertThat(response.invitations().size()).isEqualTo(3);
    }

    @Test
    void 여행_초대_목록을_조회한다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        tripRepository.save(trip);

        TripInvitationsCreateRequest request = new TripInvitationsCreateRequest(List.of(정수_ID, 홍석_ID, 준호_ID));
        invitationService.createInvitations(TRIP_ID, LEADER_ID, request);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<InvitationsResponse> invitations =
                invitationService.getTripInvitations(TRIP_ID, LEADER_ID, pageable, DATE, "desc");

        // then
        assertThat(invitations.getTotalElements()).isEqualTo(3);
    }

    @Test
    void 사용자의_여행_초대_목록을_조회한다() {
        // given
        UUID tripId1 = UUID.randomUUID();
        tripRepository.save(createTrip(tripId1));
        UUID tripId2 = UUID.randomUUID();
        tripRepository.save(createTrip(tripId2));
        UUID tripId3 = UUID.randomUUID();
        tripRepository.save(createTrip(tripId3));

        invitationService.createInvitations(tripId1, LEADER_ID, new TripInvitationsCreateRequest(List.of(홍석_ID)));
        invitationService.createInvitations(tripId2, LEADER_ID, new TripInvitationsCreateRequest(List.of(홍석_ID)));
        invitationService.createInvitations(tripId3, LEADER_ID, new TripInvitationsCreateRequest(List.of(홍석_ID)));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<MemberInvitationResponse> invitations =
                invitationService.getMemberInvitations(홍석_ID, pageable, DATE, "desc");

        // then
        assertThat(invitations.getTotalElements()).isEqualTo(3);
    }

    @Test
    void 초대를_수락하면_여행_멤버가_된다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        tripRepository.save(trip);
        Invitation invitation = new Invitation(TRIP_ID, MEMBER_ID);
        invitationRepository.save(invitation);

        // when
        MemberInvitationAcceptResponse acceptResponse =
                invitationService.acceptMemberInvitations(MEMBER_ID, TRIP_ID, invitation.getId());

        // then
        assertThat(acceptResponse.memberId()).isEqualTo(MEMBER_ID);
    }

    @Test
    void 여행_초대_목록에_회원정보_필드가_포함된다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        tripRepository.save(trip);

        TripInvitationsCreateRequest request = new TripInvitationsCreateRequest(List.of(정수_ID));
        invitationService.createInvitations(TRIP_ID, LEADER_ID, request);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<InvitationsResponse> invitations =
                invitationService.getTripInvitations(TRIP_ID, LEADER_ID, pageable, DATE, "desc");

        // then
        assertThat(invitations.getTotalElements()).isEqualTo(1);
        InvitationsResponse response = invitations.getContent().get(0);
        assertThat(response.memberId()).isEqualTo(정수_ID);
        assertThat(response.status()).isEqualTo(INVITED.name());
        // 회원 정보 필드 (Auth 연동, 스텁에서는 null)
        assertThat(response.memberName()).isNull();
        assertThat(response.memberProfileImageUrl()).isNull();
        assertThat(response.memberBio()).isNull();
    }
}
