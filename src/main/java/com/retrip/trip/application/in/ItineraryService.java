package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;
import com.retrip.trip.application.in.usecase.CreateItinerariesUseCase;
import com.retrip.trip.application.in.usecase.UpdateItinerariesUseCase;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.entity.Trip;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ItineraryService implements UpdateItinerariesUseCase, CreateItinerariesUseCase {
    private final TripRepository tripRepository;

    @Override
    public ItinerariesCreateResponse createItineraries(ItinerariesCreateRequest request) {
        Trip trip = tripRepository.findById(request.tripId())
                .orElseThrow(EntityNotFoundException::new);
        Itineraries itineraries = request.toItineraries(trip);
        return ItinerariesCreateResponse.of(trip.getId(), itineraries);
    }

    @Override
    public ItinerariesUpdateResponse updateItineraries(ItinerariesUpdateRequest request) {
        Trip trip = tripRepository.findById(request.tripId()).orElseThrow(EntityNotFoundException::new);
        trip.updateItineraries(request.toItineraries(trip), request.updateId());
        return ItinerariesUpdateResponse.of(trip.getId(), trip.getItineraries());
    }
}
