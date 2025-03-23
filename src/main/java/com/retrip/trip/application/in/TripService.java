package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.PeriodUpdateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.response.PeriodUpdateResponse;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.application.in.usecase.CreateTripUseCase;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
import com.retrip.trip.application.in.usecase.UpdatePeriodUseCase;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
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
public class TripService implements CreateTripUseCase, GetTripUseCase, UpdatePeriodUseCase {

    private final TripRepository tripRepository;
    private final TripQueryRepository tripQueryRepository;

    @Override
    public TripCreateResponse createTrip(TripCreateRequest request) {
        Trip trip = tripRepository.save(request.to());
        return TripCreateResponse.of(trip);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TripResponse> getTrips(Pageable page) {
        return tripQueryRepository.findTrips(page);
    }

    @Override
    public PeriodUpdateResponse updatePeriod(UUID tripId, PeriodUpdateRequest request) {
        Trip trip =
                tripQueryRepository
                        .findByIdWithItineraries(tripId)
                        .orElseThrow(EntityNotFoundException::new);
        trip.updatePeriod(request.toPeriod(), request.updateId());
        return PeriodUpdateResponse.of(trip.getId(), trip.getPeriod(), trip.getItineraries());
    }
}
