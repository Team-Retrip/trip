package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;
import com.retrip.trip.application.in.usecase.UpdateItinerariesUseCase;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class ItineraryService implements UpdateItinerariesUseCase {
    private final TripRepository tripRepository;

    @Override
    public ItinerariesUpdateResponse updateItineraries(ItinerariesUpdateRequest request) {
        Trip trip = tripRepository.findById(request.tripId()).orElseThrow(EntityNotFoundException::new);
        trip.updateItineraries(trip.getPeriod(), request.getDates(), request.updateId());
        return ItinerariesUpdateResponse.of(trip.getId(), trip.getItineraries());
    }
}
