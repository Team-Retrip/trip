package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripHashTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripQueryRepository {
    List<Trip> findTrips(Pageable page);

    Page<Trip> findMyTrips(UUID memberId, Pageable page);

    Optional<Trip> findByIdWithItineraries(UUID tripId);

    List<TripHashTag> findHashTags(List<Trip> trips);
}
