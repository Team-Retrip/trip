package com.retrip.trip.application.in;

import com.retrip.trip.application.in.base.BaseInvitationServiceTest;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.MemberInvitationsResponse;
import com.retrip.trip.application.in.response.InvitationsCreateResponse;
import com.retrip.trip.application.in.response.InvitationsResponse;
import com.retrip.trip.domain.entity.Trip;
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

        TripInvitationsCreateRequest request = new TripInvitationsCreateRequest(
                LEADER_ID, List.of(정수_ID, 홍석_ID, 준호_ID));
        InvitationsCreateResponse response = invitationService.createInvitations(TRIP_ID, request);
        assertThat(response.tripId()).isEqualTo(TRIP_ID);
        assertThat(response.invitations().size()).isEqualTo(3);
    }

    @Test
    void 여행_초대_목록을_조회한다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        tripRepository.save(trip);

        TripInvitationsCreateRequest request = new TripInvitationsCreateRequest(
                LEADER_ID, List.of(정수_ID, 홍석_ID, 준호_ID));
        invitationService.createInvitations(TRIP_ID, request);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<InvitationsResponse> invitations =
                invitationService.getTripInvitations(TRIP_ID, LEADER_ID, INVITED.name(), pageable, DATE, "desc");

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

        invitationService.createInvitations(tripId1, new TripInvitationsCreateRequest(LEADER_ID, List.of(홍석_ID)));
        invitationService.createInvitations(tripId2, new TripInvitationsCreateRequest(LEADER_ID, List.of(홍석_ID)));
        invitationService.createInvitations(tripId3, new TripInvitationsCreateRequest(LEADER_ID, List.of(홍석_ID)));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<MemberInvitationsResponse> invitations =
                invitationService.getMemberInvitations(홍석_ID, INVITED.name(), pageable, DATE, "desc");

        // then
        assertThat(invitations.getTotalElements()).isEqualTo(3);
    }
}
