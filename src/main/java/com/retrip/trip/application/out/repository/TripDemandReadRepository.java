package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.TripDemand;
import java.util.Optional;
import java.util.UUID;

public interface TripDemandReadRepository
        extends ReadRepository<TripDemand, UUID> {

    Optional<TripDemand> findByTripIdAndId(UUID tripId, UUID id);
}
