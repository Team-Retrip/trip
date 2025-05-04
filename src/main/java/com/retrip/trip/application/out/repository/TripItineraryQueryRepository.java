package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.Itinerary;

import java.util.List;
import java.util.UUID;

public interface TripItineraryQueryRepository {
    List<Itinerary> findByIdsWithItineraryDetails(List<UUID> ids);
}
