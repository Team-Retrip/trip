package com.retrip.trip.application.out.repository;

import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.Trip;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TripQueryRepository {
    Page<TripResponse> findTrips(Pageable page);

    Optional<Trip> findByIdWithItineraries(UUID uuid);

    Optional<Trip> findByTripIdAndDates(UUID tripId, List<LocalDate> dates);
}
