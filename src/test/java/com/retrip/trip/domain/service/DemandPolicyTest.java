package com.retrip.trip.domain.service;

import static com.retrip.trip.domain.fixture.TripFixture.LEADER_ID;
import static com.retrip.trip.domain.fixture.TripFixture.MEMBER_ID;
import static com.retrip.trip.domain.fixture.TripFixture.TRIP_ID;
import static com.retrip.trip.domain.fixture.TripFixture.createTrip;
import static com.retrip.trip.domain.fixture.TripFixture.정수_ID;
import static com.retrip.trip.domain.fixture.TripFixture.준호_ID;
import static com.retrip.trip.domain.fixture.TripFixture.지수_ID;
import static com.retrip.trip.domain.fixture.TripFixture.혁진_ID;
import static com.retrip.trip.domain.fixture.TripFixture.홍석_ID;
import static com.retrip.trip.domain.vo.InvitationStatus.EXPIRED;
import static com.retrip.trip.domain.vo.TripStatus.RECRUITING;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.entity.demand.Demand;
import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.exception.InvitationExpiredException;
import com.retrip.trip.domain.exception.InvitationRejectNotAllowedException;
import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.TripInvitationDuplicateException;
import com.retrip.trip.domain.exception.TripNotRecruitingException;
import com.retrip.trip.domain.exception.TripParticipantsIsFullException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.IllegalStateException;
import com.retrip.trip.domain.vo.InvitationStatus;
import com.retrip.trip.domain.vo.TripStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.util.ReflectionTestUtils;

class DemandPolicyTest {

    DemandPolicy demandPolicy = new DemandPolicy();

    @Test
    void 강퇴된_사용자는_참여요청을_할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        trip.addParticipant(TripParticipant.createTripParticipant(정수_ID, trip));
        trip.banMembers(LEADER_ID, List.of(정수_ID));
        List<Demand> savedDemands = List.of();

        // when, then
        assertThatThrownBy(() -> demandPolicy.canDemand(정수_ID, trip, savedDemands))
                .isExactlyInstanceOf(BusinessException.class);
    }

    @Test
    void 이미_참여요청한_사용자는_다시_요청할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Demand savedDemand = Demand.create(정수_ID, TRIP_ID, "참여 요청 합니다");
        List<Demand> savedDemands = List.of(savedDemand);

        // when & then
        assertThatThrownBy(() -> demandPolicy.canDemand(정수_ID, trip, savedDemands))
                .isExactlyInstanceOf(BusinessException.class);
    }

    @Test
    void 모집중이_아닌_여행에는_참여요청을_할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        ReflectionTestUtils.setField(trip, "status", TripStatus.IN_PROGRESS);
        List<Demand> savedDemands = List.of();

        // when & then
        assertThatThrownBy(() -> demandPolicy.canDemand(정수_ID, trip, savedDemands))
                .isExactlyInstanceOf(BusinessException.class);
    }

    @Test
    void 여행_정원이_가득차면_참여요청을_할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        trip.addParticipant(TripParticipant.createTripParticipant(홍석_ID, trip));
        trip.addParticipant(TripParticipant.createTripParticipant(준호_ID, trip));
        trip.addParticipant(TripParticipant.createTripParticipant(지수_ID, trip));
        List<Demand> savedDemands = List.of();

        // when & then
        assertThatThrownBy(() -> demandPolicy.canDemand(정수_ID, trip, savedDemands))
                .isExactlyInstanceOf(TripParticipantsIsFullException.class);
    }

    @Test
    void 정상적인_경우_참여요청을_할_수_있다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        List<Demand> savedDemands = List.of();

        // when & then
        demandPolicy.canDemand(정수_ID, trip, savedDemands);
    }

    @Test
    void 리더가_아니면_승인할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        trip.addParticipant(TripParticipant.createTripParticipant(정수_ID, trip));
        Demand demand = Demand.create(정수_ID, TRIP_ID, "참여 요청 합니다");

        // when & then
        assertThatThrownBy(() -> demandPolicy.canApprove(정수_ID, trip, demand))
                .isExactlyInstanceOf(MemberIsNotLeaderException.class);
    }

    @Test
    void Pending_상태가_아닌_Demand는_승인할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Demand demand = Demand.create(정수_ID, TRIP_ID, "참여 요청 합니다");
        demand.approve();

        // when & then
        assertThatThrownBy(() -> demandPolicy.canApprove(LEADER_ID, trip, demand))
                .isExactlyInstanceOf(BusinessException.class);
    }

    @Test
    void 정상적인_경우_승인할_수_있다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Demand demand = Demand.create(정수_ID, TRIP_ID, "참여 요청 합니다");

        // when & then
        demandPolicy.canApprove(LEADER_ID, trip, demand);
    }

    @Test
    void 리더가_아니면_거절할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        trip.addParticipant(TripParticipant.createTripParticipant(정수_ID, trip));
        Demand demand = Demand.create(정수_ID, TRIP_ID, "참여 요청 합니다");

        // when & then
        assertThatThrownBy(() -> demandPolicy.canReject(정수_ID, trip, demand))
                .isExactlyInstanceOf(MemberIsNotLeaderException.class);
    }

    @Test
    void Pending_상태가_아닌_Demand는_거절할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Demand demand = Demand.create(정수_ID, TRIP_ID, "참여 요청 합니다");
        demand.approve();

        // when & then
        assertThatThrownBy(() -> demandPolicy.canReject(LEADER_ID, trip, demand))
                .isExactlyInstanceOf(BusinessException.class);
    }

    @Test
    void 정상적인_경우_거절할_수_있다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        Demand demand = Demand.create(정수_ID, TRIP_ID, "참여 요청 합니다");

        // when & then
        demandPolicy.canReject(LEADER_ID, trip, demand);
    }

    @Test
    void 리더가_아니면_요청목록을_조회할_수_없다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        trip.addParticipant(TripParticipant.createTripParticipant(정수_ID, trip));

        // when & then
        assertThatThrownBy(() -> demandPolicy.canViewDemands(정수_ID, trip))
                .isExactlyInstanceOf(MemberIsNotLeaderException.class);
    }

    @Test
    void 리더는_요청목록을_조회할_수_있다() {
        // given
        Trip trip = createTrip(TRIP_ID);

        // when & then
        demandPolicy.canViewDemands(LEADER_ID, trip);
    }

    @Test
    void 본인의_대기중인_참여요청은_취소할_수_있다() {
        // given
        Demand demand = Demand.create(정수_ID, TRIP_ID, "참여 요청 합니다");

        // when & then
        demandPolicy.canCancel(정수_ID, demand);
    }

    @Test
    void 다른_사람의_참여요청은_취소할_수_없다() {
        // given
        Demand demand = Demand.create(정수_ID, TRIP_ID, "참여 요청 합니다");

        // when & then
        assertThatThrownBy(() -> demandPolicy.canCancel(홍석_ID, demand))
                .isExactlyInstanceOf(BusinessException.class);
    }

    @Test
    void PENDING_상태가_아닌_참여요청은_취소할_수_없다() {
        // given
        Demand demand = Demand.create(정수_ID, TRIP_ID, "참여 요청 합니다");
        demand.approve();

        // when & then
        assertThatThrownBy(() -> demandPolicy.canCancel(정수_ID, demand))
                .isExactlyInstanceOf(BusinessException.class);
    }
}
