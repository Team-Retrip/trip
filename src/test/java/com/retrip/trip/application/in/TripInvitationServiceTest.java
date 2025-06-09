package com.retrip.trip.application.in;

import com.retrip.trip.application.in.base.BaseTripInvitationServiceTest;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.TripInvitationsCreateResponse;
import com.retrip.trip.application.in.response.TripInvitationsResponse;
import com.retrip.trip.domain.entity.Trip;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.retrip.trip.application.in.request.TripInvitationOrder.DATE;
import static com.retrip.trip.domain.fixture.TripFixture.*;
import static com.retrip.trip.domain.vo.TripInvitationStatus.INVITED;
import static org.assertj.core.api.Assertions.assertThat;

class TripInvitationServiceTest extends BaseTripInvitationServiceTest {
    @Test
    void 여행_초대를_생성한다() {
        Trip trip = createTrip(TRIP_ID);
        tripRepository.save(trip);

        TripInvitationsCreateRequest request = new TripInvitationsCreateRequest(
                LEADER_ID, List.of(정수_ID, 홍석_ID, 준호_ID));
        TripInvitationsCreateResponse response = tripInvitationService.createInvitations(TRIP_ID, request);
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
        tripInvitationService.createInvitations(TRIP_ID, request);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<TripInvitationsResponse> invitations =
                tripInvitationService.getTripInvitations(TRIP_ID, leaderId, INVITED.name(), pageable, DATE, "desc");

        // then
        assertThat(invitations.getTotalElements()).isEqualTo(3);
    }
}
