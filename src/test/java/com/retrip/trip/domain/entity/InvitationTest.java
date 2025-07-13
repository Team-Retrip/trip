package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.entity.invitation.Invitation;
import org.junit.jupiter.api.Test;

import static com.retrip.trip.domain.fixture.TripFixture.*;
import static org.assertj.core.api.Assertions.assertThatCode;

class InvitationTest {
    @Test
    void 사용자_ID로_여행_초대를_생성한다() {
        assertThatCode(() -> new Invitation(TRIP_ID, MEMBER_ID))
                .doesNotThrowAnyException();
    }
}
