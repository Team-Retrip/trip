package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.response.ItineraryResponse;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetItinerariesUseCase {
    Page<ItineraryResponse> getItineraries(UUID tripId, Pageable page);

}
