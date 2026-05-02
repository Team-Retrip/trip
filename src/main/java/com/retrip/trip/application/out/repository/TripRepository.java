package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.vo.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    List<Trip> findByStatusInAndPeriodStartLessThanEqual(List<TripStatus> statuses, LocalDate date);

    List<Trip> findByStatusAndPeriodEndLessThan(TripStatus status, LocalDate date);
}
