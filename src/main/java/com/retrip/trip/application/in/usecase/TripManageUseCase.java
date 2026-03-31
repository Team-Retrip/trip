package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripUpdateRequest;
import com.retrip.trip.application.in.request.TripUpdateVisibilityRequest;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.response.TripUpdateResponse;
import com.retrip.trip.application.in.response.TripUpdateVisibilityResponse;

import java.util.List;
import java.util.UUID;

public interface TripManageUseCase {
    TripCreateResponse createTrip(UUID memberId, TripCreateRequest request);

    TripCreateResponse createTripWithItineraries(UUID memberId, TripCreateRequest request);

    TripUpdateResponse updateTrip(UUID memberId, UUID tripId, TripUpdateRequest request);

    TripUpdateVisibilityResponse updateTripVisibility(UUID memberId, UUID tripId, TripUpdateVisibilityRequest request);

    void banMembers(UUID memberId, UUID tripId, List<UUID> memberIds);
}
