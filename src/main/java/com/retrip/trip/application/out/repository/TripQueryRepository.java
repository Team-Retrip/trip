package com.retrip.trip.application.out.repository;

import com.retrip.trip.application.in.response.MyTripResponse;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.domain.entity.Trip;

import java.util.List;
import java.util.Optional;

import java.util.UUID;

import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TripQueryRepository {
    Page<TripResponse> findTrips(Pageable page);

    List<MyTripResponse> findMyTrips(List<UUID> tripIds);

    Optional<Trip> findByIdWithItineraries(UUID tripId);
}
