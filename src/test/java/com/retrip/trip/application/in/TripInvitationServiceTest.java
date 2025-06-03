package com.retrip.trip.application.in;

import com.retrip.trip.application.in.base.BaseTripInvitationServiceTest;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.TripInvitationsCreateResponse;
import com.retrip.trip.domain.entity.Trip;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.retrip.trip.domain.fixture.TripFixture.*;
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
}
