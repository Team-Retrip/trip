package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.base.BaseInvitationServiceTest;
import com.retrip.trip.application.in.request.TripInvitationsCreateRequest;
import com.retrip.trip.application.in.response.MemberInvitationAcceptResponse;
import com.retrip.trip.application.in.response.MemberInvitationResponse;
import com.retrip.trip.application.in.response.MyPageInvitationResponse;
import com.retrip.trip.application.in.response.InvitationsCreateResponse;
import com.retrip.trip.application.in.response.InvitationsResponse;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.fixture.TripFixture;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripStatus;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
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

    @Test
    void 사용자_초대_목록_조회시_INVITED_상태만_반환된다() {
        // given
        Trip trip = createTrip(TRIP_ID);
        tripRepository.save(trip);
        // INVITED 2개, ACCEPTED 1개 생성
        Invitation inv1 = new Invitation(TRIP_ID, 홍석_ID);
        Invitation inv2 = new Invitation(TRIP_ID, 준호_ID);
        Invitation inv3 = new Invitation(TRIP_ID, 정수_ID);
        invitationRepository.saveAll(List.of(inv1, inv2, inv3));
        // inv3 수락
        invitationService.acceptMemberInvitations(정수_ID, TRIP_ID, inv3.getId());

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<MemberInvitationResponse> result =
                invitationService.getMemberInvitations(홍석_ID, pageable, DATE, "desc");

        // then: 홍석_ID의 INVITED 상태 초대만 반환
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).status()).isEqualTo(INVITED.name());
    }

    @Test
    void 마이페이지_초대함_전체_목록을_조회한다() {
        // given: 3개 여행, 각각 홍석에게 초대 발송 후 하나는 수락
        UUID tripId1 = UUID.randomUUID();
        UUID tripId2 = UUID.randomUUID();
        UUID tripId3 = UUID.randomUUID();
        tripRepository.save(createTrip(tripId1));
        tripRepository.save(createTrip(tripId2));
        tripRepository.save(createTrip(tripId3));

        Invitation inv1 = new Invitation(tripId1, 홍석_ID);
        Invitation inv2 = new Invitation(tripId2, 홍석_ID);
        Invitation inv3 = new Invitation(tripId3, 홍석_ID);
        invitationRepository.saveAll(List.of(inv1, inv2, inv3));
        // inv3 수락 → ACCEPTED
        invitationService.acceptMemberInvitations(홍석_ID, tripId3, inv3.getId());

        Pageable pageable = PageRequest.of(0, 10);

        // when: 필터 없이 전체 조회
        Page<MyPageInvitationResponse> result =
                invitationService.getMyPageInvitations(홍석_ID, null, null, null, pageable);

        // then: INVITED + ACCEPTED 모두 포함
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void 마이페이지_초대함_여행상태로_필터링한다() {
        // given: RECRUITING 여행 1개, RECRUITMENT_CLOSED 여행 1개
        Trip recruitingTrip = TripFixture.createTestTrip(LEADER_ID, "모집중 여행", "설명", TripCategory.DOMESTIC);
        Trip closedTrip = TripFixture.createReadyTrip(LEADER_ID, "모집완료 여행", "설명", TripCategory.DOMESTIC);
        tripRepository.save(recruitingTrip);
        tripRepository.save(closedTrip);

        invitationRepository.save(new Invitation(recruitingTrip.getId(), 홍석_ID));
        invitationRepository.save(new Invitation(closedTrip.getId(), 홍석_ID));

        Pageable pageable = PageRequest.of(0, 10);

        // when: RECRUITING 만 필터
        Page<MyPageInvitationResponse> result =
                invitationService.getMyPageInvitations(홍석_ID, List.of(TripStatus.RECRUITING), null, null, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).tripStatus()).isEqualTo(TripStatus.RECRUITING.name());
    }

    @Test
    void 마이페이지_초대함_국내_해외로_필터링한다() {
        // given: 국내 여행 1개, 해외 여행 1개
        Trip domesticTrip = TripFixture.createTestTrip(LEADER_ID, "국내 여행", "설명", TripCategory.DOMESTIC);
        Trip overseasTrip = TripFixture.createTestTrip(LEADER_ID, "해외 여행", "설명", TripCategory.OVERSEAS);
        tripRepository.save(domesticTrip);
        tripRepository.save(overseasTrip);

        invitationRepository.save(new Invitation(domesticTrip.getId(), 홍석_ID));
        invitationRepository.save(new Invitation(overseasTrip.getId(), 홍석_ID));

        Pageable pageable = PageRequest.of(0, 10);

        // when: 해외만 필터
        Page<MyPageInvitationResponse> result =
                invitationService.getMyPageInvitations(홍석_ID, null, TripCategory.OVERSEAS, null, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).tripCategory()).isEqualTo(TripCategory.OVERSEAS.name());
    }

    @Test
    void 마이페이지_초대함_기간_연도로_필터링한다() {
        // given: invitedAt이 올해인 초대, 2년 전 초대
        Trip trip1 = TripFixture.createTestTrip(LEADER_ID, "여행1", "설명", TripCategory.DOMESTIC);
        Trip trip2 = TripFixture.createTestTrip(LEADER_ID, "여행2", "설명", TripCategory.DOMESTIC);
        tripRepository.save(trip1);
        tripRepository.save(trip2);

        Invitation recentInv = new Invitation(trip1.getId(), 홍석_ID);
        Invitation oldInv = new Invitation(trip2.getId(), 홍석_ID);
        // 2년 전 날짜로 설정
        ReflectionTestUtils.setField(oldInv, "invitedAt", LocalDateTime.now().minusYears(2));
        invitationRepository.saveAll(List.of(recentInv, oldInv));

        int currentYear = LocalDateTime.now().getYear();
        Pageable pageable = PageRequest.of(0, 10);

        // when: 올해 연도로 필터
        Page<MyPageInvitationResponse> result =
                invitationService.getMyPageInvitations(홍석_ID, null, null, String.valueOf(currentYear), pageable);

        // then: 올해 초대만 반환
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).invitedAt().getYear()).isEqualTo(currentYear);
    }

    @Test
    void 마이페이지_초대함_최근6개월로_필터링한다() {
        // given: 최근 초대, 1년 전 초대
        Trip trip1 = TripFixture.createTestTrip(LEADER_ID, "최근 여행", "설명", TripCategory.DOMESTIC);
        Trip trip2 = TripFixture.createTestTrip(LEADER_ID, "오래된 여행", "설명", TripCategory.DOMESTIC);
        tripRepository.save(trip1);
        tripRepository.save(trip2);

        Invitation recentInv = new Invitation(trip1.getId(), 홍석_ID);
        Invitation oldInv = new Invitation(trip2.getId(), 홍석_ID);
        ReflectionTestUtils.setField(oldInv, "invitedAt", LocalDateTime.now().minusMonths(8));
        invitationRepository.saveAll(List.of(recentInv, oldInv));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<MyPageInvitationResponse> result =
                invitationService.getMyPageInvitations(홍석_ID, null, null, "RECENT_6_MONTHS", pageable);

        // then: 최근 6개월 이내 초대만 반환
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}
