package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.response.ItineraryResponse;

import java.util.List;
import java.util.UUID;

public interface GetItinerariesUseCase {
    List<ItineraryResponse> getItineraries(UUID tripId);
}