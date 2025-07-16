package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripConfirmationDemand;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TripConfirmationDemandRepository extends JpaRepository<TripConfirmationDemand, UUID> {
    Optional<TripConfirmationDemand> findByTrip(Trip trip);

    @Query("SELECT r FROM TripConfirmationDemand r WHERE r.expired = false AND r.confirmEndDate < CURRENT_DATE")
    List<TripConfirmationDemand> findAllNonExpiredPastDeadline();

}
