package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.in.usecase.GetItinerariesUseCase;
import com.retrip.trip.application.in.usecase.UpdateItinerariesUseCase;
import com.retrip.trip.application.out.repository.ItineraryQueryRepository;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.Trip;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class ItineraryService implements UpdateItinerariesUseCase, GetItinerariesUseCase {
    private final TripQueryRepository tripQueryRepository;
    private final ItineraryQueryRepository itineraryQueryRepository;


    @Override
    public ItinerariesUpdateResponse updateItineraries(ItinerariesUpdateRequest request) {
        Trip trip = tripQueryRepository.findByIdWithItineraries(request.tripId()).orElseThrow(EntityNotFoundException::new);
        List<Itinerary> itineraries = request.toItineraries(trip);
        trip.updateItineraries(itineraries, request.updateId());
        return ItinerariesUpdateResponse.of(trip.getId(), trip.getItineraries());
    }

    @Override
    public Page<ItineraryResponse> getItineraries(UUID tripId, Pageable page) {
        return itineraryQueryRepository.findItineraries(tripId, page);
    }
}
