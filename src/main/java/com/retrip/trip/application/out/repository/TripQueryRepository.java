package com.retrip.trip.application.out.repository;

import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.domain.entity.Trip;

import java.util.Optional;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TripQueryRepository {
  Page<TripResponse> findTrips(Pageable page);

  Optional<Trip> findByIdWithItineraries(UUID tripId);
}
