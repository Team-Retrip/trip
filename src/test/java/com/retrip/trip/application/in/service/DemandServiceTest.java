package com.retrip.trip.application.in.service;

import static com.retrip.trip.domain.fixture.TripFixture.LEADER_ID;
import static com.retrip.trip.domain.fixture.TripFixture.정수_ID;
import static com.retrip.trip.domain.fixture.TripFixture.지수_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.retrip.trip.application.in.base.BaseDemandServiceTest;
import com.retrip.trip.application.in.request.demand.TripDemandRequest;
import com.retrip.trip.application.in.response.demand.DemandApproveResponse;
import com.retrip.trip.application.in.response.demand.DemandResponse;
import com.retrip.trip.application.in.response.demand.DemandRejectResponse;
import com.retrip.trip.application.in.response.demand.DemandsResponse;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.entity.demand.Demand;
import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.TripParticipantsIsFullException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.fixture.TripFixture;
import com.retrip.trip.domain.vo.DemandStatus;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripStatus;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;


class DemandServiceTest extends BaseDemandServiceTest {

    private Trip createTestTripWithParticipants() {
        Trip trip = TripFixture.createTestTripWithParticipants();
        return tripRepository.save(trip);
    }

    private Trip createTestTrip(String title, String description, TripCategory category) {
        Trip trip = TripFixture.createTestTrip(LEADER_ID, title, description, category, TripStatus.RECRUITING);
        return tripRepository.save(trip);
    }

    private Demand createTestDemand(UUID tripId, String message) {
        Demand demand = Demand.create(정수_ID, tripId, message);
        return demandRepository.save(demand);
    }

    @Test
    void 사용자가_참여_요청을_보낸다() {
        // given
        Trip trip = createTestTrip("승인 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        TripDemandRequest request = new TripDemandRequest("참여 요청 메시지");

        // when
        DemandResponse response = demandService.demand(정수_ID, trip.getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.tripId()).isEqualTo(trip.getId());
        assertThat(response.memberId()).isEqualTo(정수_ID);
        assertThat(response.statusName()).isEqualTo("대기");
    }

    @Test
    void 리더가_참여_요청을_승인하면_실제_참여자로_등록된다() {
        // given
        Trip newTrip = createTestTrip("승인 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        Demand demand = createTestDemand(newTrip.getId(), "참여 요청 메시지");

        // then
        DemandApproveResponse response = demandService.approve(LEADER_ID, newTrip.getId(), demand.getId());
        Trip trip = tripRepository.findById(newTrip.getId()).orElseThrow();
        UUID newParticipantMemberId = trip.getTripParticipants().getValues().stream()
                .map(TripParticipant::getMemberId)
                .filter(id -> id.equals(정수_ID))
                .findFirst()
                .orElseThrow();

        // when
        assertThat(response).isNotNull();
        assertThat(newParticipantMemberId).isEqualTo(정수_ID);
        assertThat(response.statusCode()).isEqualTo(DemandStatus.APPROVED.getCode());
    }

    @Test
    void 리더가_아니면_참여_요청을_승인할_수_없다() {
        // given
        Trip newTrip = createTestTrip("승인 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        Demand demand = createTestDemand(newTrip.getId(), "참여 요청 메시지");
        demand.approve();
        newTrip.addParticipant(TripParticipant.createTripParticipant(demand.getMemberId(), newTrip));

        // then && when
        assertThrows(BusinessException.class, () -> demandService.approve(정수_ID, newTrip.getId(), demand.getId()));
    }

    @Test
    void 리더가_참여_요청을_거절하면_요청_상태가_거절로_변경된다() {
        // given
        Trip newTrip = createTestTrip("승인 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        Demand demand = createTestDemand(newTrip.getId(), "참여 요청 메시지");

        // then
        DemandRejectResponse response = demandService.reject(LEADER_ID, newTrip.getId(), demand.getId());

        // when
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(DemandStatus.REJECTED.getCode());
    }

    @Test
    void 리더가_아니면_참여_요청을_거절할_수_없다() {
        // given
        Trip newTrip = createTestTrip("승인 테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        Demand demand = createTestDemand(newTrip.getId(), "참여 요청 메시지");
        demand.approve();
        newTrip.addParticipant(TripParticipant.createTripParticipant(demand.getMemberId(), newTrip));

        // then && when
        assertThrows(BusinessException.class, () -> demandService.reject(정수_ID, newTrip.getId(), demand.getId()));
    }

    @Test
    void 해당_여행에_강퇴당한_사용자는_다시_참여요청할_수_없다() {
        // given
        Trip trip = createTestTripWithParticipants();
        trip.banMembers(LEADER_ID, List.of(정수_ID));

        TripDemandRequest request = new TripDemandRequest("강퇴당한 후 다시 참여 요청 메시지");

        // when && then
        assertThrows(BusinessException.class, () -> demandService.demand(정수_ID, trip.getId(), request));
    }

    @Test
    void 여행에_참가자_안원_충족시_새로운_사용자는_참여_요청을_할_수_없다() {
        // given
        Trip trip = createTestTripWithParticipants();
        TripDemandRequest newRequest = new TripDemandRequest("저도 참여하고 싶어요!");

        // when & then
        assertThrows(TripParticipantsIsFullException.class, () -> demandService.demand(지수_ID, trip.getId(), newRequest));
    }

    @Test
    void 리더는_참여요청_목록을_조회할_수_있다() {
        // given
        Trip trip = createTestTripWithParticipants();
        // demand 생성
        createTestDemand(trip.getId(), "message1");
        createTestDemand(trip.getId(), "message2");

        // when
        List<DemandsResponse> responses = demandService.getDemands(LEADER_ID, trip.getId());

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).tripId()).isEqualTo(trip.getId());
        assertThat(responses.get(1).tripId()).isEqualTo(trip.getId());
    }

    @Test
    void 리더가_아니면_참여요청_목록을_조회할_수_없다() {
        // given
        Trip trip = createTestTripWithParticipants();
        createTestDemand(trip.getId(), "message");

        // when, then
        assertThrows(MemberIsNotLeaderException.class,
                () -> demandService.getDemands(정수_ID, trip.getId()));
    }
}
