package com.retrip.trip.application.in.service;

import static com.retrip.trip.domain.fixture.TripFixture.LEADER_ID;
import static com.retrip.trip.domain.fixture.TripFixture.정수_ID;
import static com.retrip.trip.domain.fixture.TripFixture.홍석_ID;
import static com.retrip.trip.domain.fixture.TripFixture.지수_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.retrip.trip.application.in.base.BaseDemandServiceTest;
import com.retrip.trip.application.in.request.demand.TripDemandRequest;
import com.retrip.trip.application.in.response.MyPageDemandResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


class DemandServiceTest extends BaseDemandServiceTest {

    private Trip createTestTripWithParticipants() {
        Trip trip = TripFixture.createTestTripWithParticipants();
        return tripRepository.save(trip);
    }

    private Trip createTestTrip(String title, String description, TripCategory category) {
        Trip trip = TripFixture.createTestTrip(LEADER_ID, title, description, category);
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
        DemandResponse response = demandService.demand(정수_ID, "박정수", trip.getId(), request);

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
        assertThrows(BusinessException.class, () -> demandService.demand(정수_ID, "박정수", trip.getId(), request));
    }

    @Test
    void 여행에_참가자_안원_충족시_새로운_사용자는_참여_요청을_할_수_없다() {
        // given
        Trip trip = createTestTripWithParticipants();
        TripDemandRequest newRequest = new TripDemandRequest("저도 참여하고 싶어요!");

        // when & then
        assertThrows(TripParticipantsIsFullException.class, () -> demandService.demand(지수_ID, "백지수", trip.getId(), newRequest));
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

    @Test
    void 참여요청_목록에_신청자_회원정보_필드가_포함된다() {
        // given
        Trip trip = createTestTripWithParticipants();
        createTestDemand(trip.getId(), "참여 요청 메시지");

        // when
        List<DemandsResponse> responses = demandService.getDemands(LEADER_ID, trip.getId());

        // then
        // Auth 스텁이 빈 리스트 반환하므로 memberName 등은 null이지만 필드 자체는 존재해야 함
        assertThat(responses).hasSize(1);
        DemandsResponse response = responses.get(0);
        assertThat(response.memberId()).isEqualTo(정수_ID);
        assertThat(response.message()).isEqualTo("참여 요청 메시지");
        assertThat(response.statusCode()).isEqualTo(DemandStatus.PENDING.name());
        // 회원 정보 필드 (Auth 연동, 스텁에서는 null)
        assertThat(response.memberName()).isNull();
        assertThat(response.memberProfileImageUrl()).isNull();
        assertThat(response.memberBio()).isNull();
    }

    @Test
    void 마이페이지_신청함_전체_목록을_조회한다() {
        // given: 3개 여행에 신청
        Trip trip1 = createTestTrip("여행1", "설명", TripCategory.DOMESTIC);
        Trip trip2 = createTestTrip("여행2", "설명", TripCategory.DOMESTIC);
        Trip trip3 = createTestTrip("여행3", "설명", TripCategory.OVERSEAS);
        demandRepository.save(Demand.create(홍석_ID, trip1.getId(), "신청1"));
        demandRepository.save(Demand.create(홍석_ID, trip2.getId(), "신청2"));
        demandRepository.save(Demand.create(홍석_ID, trip3.getId(), "신청3"));

        Pageable pageable = PageRequest.of(0, 10);

        // when: 필터 없이 전체 조회
        Page<MyPageDemandResponse> result =
                demandService.getMyPageDemands(홍석_ID, null, null, null, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void 마이페이지_신청함_여행상태로_필터링한다() {
        // given: RECRUITING 여행 1개, RECRUITMENT_CLOSED 여행 1개에 신청
        Trip recruitingTrip = createTestTrip("모집중 여행", "설명", TripCategory.DOMESTIC);
        Trip closedTrip = TripFixture.createReadyTrip(LEADER_ID, "모집완료 여행", "설명", TripCategory.DOMESTIC);
        tripRepository.save(closedTrip);
        demandRepository.save(Demand.create(홍석_ID, recruitingTrip.getId(), "신청1"));
        demandRepository.save(Demand.create(홍석_ID, closedTrip.getId(), "신청2"));

        Pageable pageable = PageRequest.of(0, 10);

        // when: RECRUITING 만 필터
        Page<MyPageDemandResponse> result =
                demandService.getMyPageDemands(홍석_ID, List.of(TripStatus.RECRUITING), null, null, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).tripStatus()).isEqualTo(TripStatus.RECRUITING.name());
    }

    @Test
    void 마이페이지_신청함_국내_해외로_필터링한다() {
        // given: 국내 여행 1개, 해외 여행 2개에 신청
        Trip domesticTrip = createTestTrip("국내 여행", "설명", TripCategory.DOMESTIC);
        Trip overseasTrip1 = createTestTrip("해외 여행1", "설명", TripCategory.OVERSEAS);
        Trip overseasTrip2 = createTestTrip("해외 여행2", "설명", TripCategory.OVERSEAS);
        demandRepository.save(Demand.create(홍석_ID, domesticTrip.getId(), "신청1"));
        demandRepository.save(Demand.create(홍석_ID, overseasTrip1.getId(), "신청2"));
        demandRepository.save(Demand.create(홍석_ID, overseasTrip2.getId(), "신청3"));

        Pageable pageable = PageRequest.of(0, 10);

        // when: 해외만 필터
        Page<MyPageDemandResponse> result =
                demandService.getMyPageDemands(홍석_ID, null, TripCategory.OVERSEAS, null, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(r -> r.tripCategory().equals(TripCategory.OVERSEAS.name()));
    }

    @Test
    void 마이페이지_신청함_기간_연도로_필터링한다() {
        // given: 현재 연도에 신청한 것만 있음
        Trip trip1 = createTestTrip("여행1", "설명", TripCategory.DOMESTIC);
        Trip trip2 = createTestTrip("여행2", "설명", TripCategory.DOMESTIC);
        demandRepository.save(Demand.create(홍석_ID, trip1.getId(), "신청1"));
        demandRepository.save(Demand.create(홍석_ID, trip2.getId(), "신청2"));

        int currentYear = java.time.LocalDateTime.now().getYear();
        Pageable pageable = PageRequest.of(0, 10);

        // when: 올해 연도로 필터
        Page<MyPageDemandResponse> result =
                demandService.getMyPageDemands(홍석_ID, null, null, String.valueOf(currentYear), pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void 마이페이지_신청함_최근6개월로_필터링한다() {
        // given: 현재 시점에 생성된 신청 2개 (모두 최근 6개월 이내)
        Trip trip1 = createTestTrip("여행1", "설명", TripCategory.DOMESTIC);
        Trip trip2 = createTestTrip("여행2", "설명", TripCategory.DOMESTIC);
        demandRepository.save(Demand.create(홍석_ID, trip1.getId(), "신청1"));
        demandRepository.save(Demand.create(홍석_ID, trip2.getId(), "신청2"));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<MyPageDemandResponse> result =
                demandService.getMyPageDemands(홍석_ID, null, null, "RECENT_6_MONTHS", pageable);

        // then: 모두 최근 6개월 이내이므로 전부 반환
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void 마이페이지_신청함_여행상태와_카테고리를_복합_필터링한다() {
        // given
        Trip domesticRecruiting = createTestTrip("국내 모집중", "설명", TripCategory.DOMESTIC);
        Trip overseasRecruiting = createTestTrip("해외 모집중", "설명", TripCategory.OVERSEAS);
        Trip domesticClosed = TripFixture.createReadyTrip(LEADER_ID, "국내 모집완료", "설명", TripCategory.DOMESTIC);
        tripRepository.save(domesticClosed);

        demandRepository.save(Demand.create(홍석_ID, domesticRecruiting.getId(), "신청1"));
        demandRepository.save(Demand.create(홍석_ID, overseasRecruiting.getId(), "신청2"));
        demandRepository.save(Demand.create(홍석_ID, domesticClosed.getId(), "신청3"));

        Pageable pageable = PageRequest.of(0, 10);

        // when: 국내 + 모집중
        Page<MyPageDemandResponse> result =
                demandService.getMyPageDemands(홍석_ID, List.of(TripStatus.RECRUITING), TripCategory.DOMESTIC, null, pageable);

        // then: 국내 모집중 여행 신청 1개만
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).tripCategory()).isEqualTo(TripCategory.DOMESTIC.name());
        assertThat(result.getContent().get(0).tripStatus()).isEqualTo(TripStatus.RECRUITING.name());
    }
}
