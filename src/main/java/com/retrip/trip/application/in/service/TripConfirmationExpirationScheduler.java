package com.retrip.trip.application.in.service;

import com.retrip.trip.application.out.repository.TripConfirmationDemandRepository;
import com.retrip.trip.domain.entity.TripConfirmationDemand;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.ConcreteProxy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TripConfirmationExpirationScheduler {
    private final TripConfirmationDemandRepository tripConfirmationDemandRepository;

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시에 실행
    @Transactional
    public void expireOldConfirmationDemands() {
        tripConfirmationDemandRepository.findAllNonExpiredPastDeadline().forEach(TripConfirmationDemand::expire);
    }
}
