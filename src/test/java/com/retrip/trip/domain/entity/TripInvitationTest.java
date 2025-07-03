package com.retrip.trip.domain.entity;

import org.junit.jupiter.api.Test;

import static com.retrip.trip.domain.fixture.TripFixture.*;
import static org.assertj.core.api.Assertions.assertThatCode;

class TripInvitationTest {
    @Test
    void 사용자_ID로_여행_초대를_생성한다() {
        assertThatCode(() -> new TripInvitation(createTrip(TRIP_ID), MEMBER_ID))
                .doesNotThrowAnyException();
    }
}
