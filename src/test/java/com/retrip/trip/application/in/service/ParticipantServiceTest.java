package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.base.BaseParticipantServiceTest;
import com.retrip.trip.application.in.request.TripJoinWithPasswordRequest;
import com.retrip.trip.application.in.response.TripJoinWithPasswordResponse;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripPassword;
import org.junit.jupiter.api.Test;

import static com.retrip.trip.domain.fixture.TripFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class ParticipantServiceTest extends BaseParticipantServiceTest {
    @Test
    void 비밀번호로_여행에_참여한다() {
        // given
        String password = "12345667890";
        Trip trip = createTrip(TRIP_ID);
        String passwordHash = tripPasswordEncoder.encode(password);
        TripPassword tripPassword = new TripPassword(password, passwordHash);
        trip.assignPassword(tripPassword);
        tripRepository.save(trip);
        TripJoinWithPasswordRequest request = new TripJoinWithPasswordRequest(password);

        // when
        TripJoinWithPasswordResponse response = participantService.joinTripWithPassword(TRIP_ID, request, MEMBER_ID);

        // then
        assertThat(response.participantId()).isEqualTo(MEMBER_ID);
    }
}
