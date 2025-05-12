package com.retrip.trip.application.out.repository;

import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.domain.entity.Itinerary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TripItineraryQueryRepository {
    List<Itinerary> findByIdsWithItineraryDetails(List<UUID> ids);

    Optional<Itinerary> findByIdWithItineraryDetail(UUID itineraryId, UUID itineraryDetailsId);

    Optional<Itinerary> findByIdWithItineraryDetails(UUID itineraryId);

    Page<ItineraryResponse> findItineraries(UUID tripId, Pageable page);
}
