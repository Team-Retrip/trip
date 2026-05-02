package com.retrip.trip.infra.adapter.in.scheduler;

import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TripStatusScheduler {

    private final TripRepository tripRepository;

    /**
     * 여행 시작일이 된 여행을 IN_PROGRESS로, 여행 종료일이 지난 여행을 COMPLETED로 변경합니다.
     * 초대장 만료 스케쥴러(00:05) 이후인 매일 00:10에 실행합니다.
     */
    @Scheduled(cron = "0 10 0 * * *")
    @Transactional
    public void updateTripStatuses() {
        LocalDate today = LocalDate.now();

        List<Trip> tripsToStart = tripRepository.findByStatusInAndPeriodStartLessThanEqual(
                List.of(TripStatus.RECRUITING, TripStatus.RECRUITMENT_CLOSED), today);
        tripsToStart.forEach(trip -> {
            trip.changeStatusToInProgress();
            log.info("[TripStatusScheduler] IN_PROGRESS 전환: tripId={}", trip.getId());
        });

        List<Trip> tripsToComplete = tripRepository.findByStatusAndPeriodEndLessThan(TripStatus.IN_PROGRESS, today);
        tripsToComplete.forEach(trip -> {
            trip.changeStatusToCompleted();
            log.info("[TripStatusScheduler] COMPLETED 전환: tripId={}", trip.getId());
        });

        log.info("[TripStatusScheduler] 완료 - 여행중 전환: {}건, 여행후 전환: {}건",
                tripsToStart.size(), tripsToComplete.size());
    }
}