package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.ItinerariesUpdateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsUpdateRequest;
import com.retrip.trip.application.in.response.ItinerariesUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryDetailsUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.in.usecase.GetItinerariesUseCase;
import com.retrip.trip.application.in.usecase.ManageItinerariesUseCase;
import com.retrip.trip.application.out.repository.ItineraryQueryRepository;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.ItineraryDetail;
import com.retrip.trip.domain.entity.Trip;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class ItineraryService implements ManageItinerariesUseCase, GetItinerariesUseCase {

    private final TripQueryRepository tripQueryRepository;
    private final ItineraryQueryRepository itineraryQueryRepository;

    @Override
    public ItinerariesUpdateResponse updateItineraries(
            UUID tripId, ItinerariesUpdateRequest request) {
        Trip trip =
                tripQueryRepository
                        .findByTripIdAndDates(tripId, request.toDates())
                        .orElseThrow(EntityNotFoundException::new);
        trip.updateItineraries(request.toDates());

        return ItinerariesUpdateResponse.of(trip.getItineraries().getValues());
    }

    @Override
    public ItineraryDetailsUpdateResponse updateItineraryDetails(
            UUID tripId, UUID itineraryId, ItineraryDetailsUpdateRequest request) {
        Trip trip =
                tripQueryRepository
                        .findByIdAndItineraryId(tripId, itineraryId)
                        .orElseThrow(EntityNotFoundException::new);
        Itinerary itinerary = trip.getUpdateItinerary(itineraryId);
        return ItineraryDetailsUpdateResponse.of(
                trip.updateItineraryDetails(itineraryId, request.to(itinerary)));
    }

    @Override
    public Page<ItineraryResponse> getItineraries(UUID tripId, Pageable page) {
        return itineraryQueryRepository.findItineraries(tripId, page);
    }
}
