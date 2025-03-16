package com.retrip.trip.application.out.repository;

import com.retrip.trip.application.in.response.ItineraryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ItineraryQueryRepository {
    Page<ItineraryResponse> findItineraries(UUID tripId, Pageable page);
}
