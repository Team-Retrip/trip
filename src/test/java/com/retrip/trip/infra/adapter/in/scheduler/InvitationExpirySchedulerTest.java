package com.retrip.trip.infra.adapter.in.scheduler;

import com.retrip.trip.application.in.base.BaseServiceTest;
import com.retrip.trip.application.out.repository.InvitationRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.fixture.TripFixture;
import com.retrip.trip.domain.vo.InvitationStatus;
import com.retrip.trip.domain.vo.TripCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.retrip.trip.domain.fixture.TripFixture.LEADER_ID;
import static com.retrip.trip.domain.fixture.TripFixture.홍석_ID;
import static com.retrip.trip.domain.fixture.TripFixture.준호_ID;
import static com.retrip.trip.domain.fixture.TripFixture.정수_ID;
import static com.retrip.trip.domain.vo.InvitationStatus.ACCEPTED;
import static com.retrip.trip.domain.vo.InvitationStatus.EXPIRED;
import static com.retrip.trip.domain.vo.InvitationStatus.INVITED;
import static com.retrip.trip.domain.vo.InvitationStatus.REJECTED;
import static org.assertj.core.api.Assertions.assertThat;

class InvitationExpirySchedulerTest extends BaseServiceTest {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private TripRepository tripRepository;

    private InvitationExpiryScheduler scheduler;

    @BeforeEach
    void setUp() {
        invitationRepository.deleteAll();
        tripRepository.deleteAll();
        scheduler = new InvitationExpiryScheduler(invitationRepository);
    }

    private Trip createAndSaveTrip() {
        Trip trip = TripFixture.createTestTrip(LEADER_ID, "테스트 여행", "여행 설명", TripCategory.DOMESTIC);
        return tripRepository.save(trip);
    }

    private Invitation createExpiredInvitation(UUID tripId, UUID memberId) {
        Invitation invitation = new Invitation(tripId, memberId);
        ReflectionTestUtils.setField(invitation, "expiresAt", LocalDateTime.now().minusDays(1));
        return invitation;
    }

    @Test
    void 만료_기한이_지난_INVITED_초대장은_EXPIRED로_변경된다() {
        // given
        Trip trip = createAndSaveTrip();
        Invitation expiredInv1 = createExpiredInvitation(trip.getId(), 홍석_ID);
        Invitation expiredInv2 = createExpiredInvitation(trip.getId(), 준호_ID);
        invitationRepository.saveAll(List.of(expiredInv1, expiredInv2));

        // when
        scheduler.expireInvitations();

        // then
        List<Invitation> result = invitationRepository.findAll();
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(inv -> inv.getStatus() == EXPIRED);
    }

    @Test
    void 만료_기한이_남은_초대장은_상태가_변경되지_않는다() {
        // given
        Trip trip = createAndSaveTrip();
        Invitation validInv = new Invitation(trip.getId(), 홍석_ID); // expiresAt = 초대일 + 5일
        invitationRepository.save(validInv);

        // when
        scheduler.expireInvitations();

        // then
        Invitation result = invitationRepository.findById(validInv.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(INVITED);
    }

    @Test
    void 만료된_초대장과_유효한_초대장이_섞여있을때_만료된것만_변경된다() {
        // given
        Trip trip = createAndSaveTrip();
        Invitation expiredInv = createExpiredInvitation(trip.getId(), 홍석_ID);
        Invitation validInv = new Invitation(trip.getId(), 준호_ID);
        invitationRepository.saveAll(List.of(expiredInv, validInv));

        // when
        scheduler.expireInvitations();

        // then
        Invitation expired = invitationRepository.findById(expiredInv.getId()).orElseThrow();
        Invitation valid = invitationRepository.findById(validInv.getId()).orElseThrow();
        assertThat(expired.getStatus()).isEqualTo(EXPIRED);
        assertThat(valid.getStatus()).isEqualTo(INVITED);
    }

    @Test
    void ACCEPTED_상태의_초대장은_만료_처리되지_않는다() {
        // given
        Trip trip = createAndSaveTrip();
        Invitation invitation = createExpiredInvitation(trip.getId(), 홍석_ID);
        ReflectionTestUtils.setField(invitation, "status", ACCEPTED);
        invitationRepository.save(invitation);

        // when
        scheduler.expireInvitations();

        // then
        Invitation result = invitationRepository.findById(invitation.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(ACCEPTED);
    }

    @Test
    void REJECTED_상태의_초대장은_만료_처리되지_않는다() {
        // given
        Trip trip = createAndSaveTrip();
        Invitation invitation = createExpiredInvitation(trip.getId(), 홍석_ID);
        ReflectionTestUtils.setField(invitation, "status", REJECTED);
        invitationRepository.save(invitation);

        // when
        scheduler.expireInvitations();

        // then
        Invitation result = invitationRepository.findById(invitation.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(REJECTED);
    }

    @Test
    void 이미_EXPIRED인_초대장은_재처리되지_않는다() {
        // given
        Trip trip = createAndSaveTrip();
        Invitation invitation = createExpiredInvitation(trip.getId(), 홍석_ID);
        ReflectionTestUtils.setField(invitation, "status", EXPIRED);
        invitationRepository.save(invitation);

        // when
        scheduler.expireInvitations();

        // then: 예외 없이 정상 처리, 상태 유지
        Invitation result = invitationRepository.findById(invitation.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(EXPIRED);
    }

    @Test
    void 만료_대상_초대장이_없으면_아무것도_변경되지_않는다() {
        // given: 유효한 초대장만 존재
        Trip trip = createAndSaveTrip();
        invitationRepository.save(new Invitation(trip.getId(), 정수_ID));

        // when
        scheduler.expireInvitations();

        // then
        List<Invitation> result = invitationRepository.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(INVITED);
    }
}
