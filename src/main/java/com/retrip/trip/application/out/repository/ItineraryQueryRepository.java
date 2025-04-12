package com.retrip.trip.application.out.repository;

import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.Trip;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ItineraryQueryRepository {
    Page<ItineraryResponse> findItineraries(UUID tripId, Pageable page);

    // Optional<Itinerary> findByIdWithItineraryDetails(UUID itineraryId);
}
