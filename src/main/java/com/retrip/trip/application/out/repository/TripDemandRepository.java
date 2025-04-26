package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.TripDemand;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripDemandRepository
        extends ReadRepository<TripDemand, UUID> {

    Optional<TripDemand> findByTripIdAndId(UUID tripId, UUID id);
}