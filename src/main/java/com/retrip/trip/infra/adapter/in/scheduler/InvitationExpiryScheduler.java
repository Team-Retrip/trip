package com.retrip.trip.infra.adapter.in.scheduler;

import com.retrip.trip.application.out.repository.InvitationRepository;
import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.vo.InvitationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvitationExpiryScheduler {

    private final InvitationRepository invitationRepository;

    /**
     * 만료된 초대장 상태를 EXPIRED로 업데이트합니다.
     * 초대장 만료 기한은 초대일로부터 5일 후 자정(00:00)이므로,
     * 매일 00:05에 실행하여 자정 이후 만료된 초대장을 일괄 처리합니다.
     */
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void expireInvitations() {
        LocalDateTime now = LocalDateTime.now();
        List<Invitation> expiredInvitations = invitationRepository.findByStatusAndExpiresAtBefore(InvitationStatus.INVITED, now);

        if (expiredInvitations.isEmpty()) {
            return;
        }

        expiredInvitations.forEach(Invitation::expire);
        log.info("[InvitationExpiryScheduler] 만료 처리된 초대장 수: {}", expiredInvitations.size());
    }
}
