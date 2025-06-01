package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.ItineraryDetailsCreateRequest;
import com.retrip.trip.application.in.request.ItineraryDetailsUpdateRequest;
import com.retrip.trip.application.in.response.ItineraryDetailsCreateResponse;
import com.retrip.trip.application.in.response.ItineraryDetailsUpdateResponse;
import com.retrip.trip.application.in.response.ItineraryResponse;
import com.retrip.trip.application.in.usecase.GetItinerariesUseCase;
import com.retrip.trip.application.in.usecase.ManageItineraryDetailsUseCase;
import com.retrip.trip.application.out.repository.TripItineraryQueryRepository;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.ItineraryDetail;
import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ItineraryService implements ManageItineraryDetailsUseCase, GetItinerariesUseCase {
    private final TripItineraryQueryRepository tripItineraryQueryRepository;

    @Override
    public ItineraryDetailsCreateResponse createItineraryDetails(UUID tripId, UUID itineraryId,
                                                                 ItineraryDetailsCreateRequest request) {
        Itinerary itinerary = tripItineraryQueryRepository.findByIdWithItineraryDetails(itineraryId)
                .orElseThrow(EntityNotFoundException::new);
        ItineraryDetail itineraryDetail = request.to(itinerary);
        itinerary.addItineraryDetail(itineraryDetail);
        return ItineraryDetailsCreateResponse.of(itineraryDetail);
    }

    @Override
    public ItineraryDetailsUpdateResponse updateItineraryDetails(
            UUID tripId,
            UUID itineraryId,
            UUID itineraryDetailId,
            ItineraryDetailsUpdateRequest request) {
        Itinerary itinerary = tripItineraryQueryRepository.findByIdWithItineraryDetails(itineraryId)
                .orElseThrow(EntityNotFoundException::new);
        ItineraryDetail itineraryDetail = request.to(itinerary);
        itinerary.updateItineraryDetail(itineraryDetail, itineraryDetailId);
        return ItineraryDetailsUpdateResponse.of(itineraryDetail);
    }

    @Override
    public void deleteItineraryDetail(UUID tripId, UUID itineraryId, UUID itineraryDetailsId) {
        Itinerary itinerary = tripItineraryQueryRepository.findByIdWithItineraryDetail(itineraryId, itineraryDetailsId)
                .orElseThrow(EntityNotFoundException::new);
        itinerary.removeItineraryDetail(itineraryDetailsId);
    }

    @Override
    public Page<ItineraryResponse> getItineraries(UUID tripId, Pageable page) {
        return tripItineraryQueryRepository.findItineraries(tripId, page);
    }
}
