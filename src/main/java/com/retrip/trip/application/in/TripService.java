package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.PeriodUpdateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;
import com.retrip.trip.application.in.response.PeriodUpdateResponse;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.application.in.usecase.CreateItinerariesUseCase;
import com.retrip.trip.application.in.usecase.CreateTripUseCase;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
import com.retrip.trip.application.in.usecase.UpdatePeriodUseCase;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.entity.Trip;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class TripService implements CreateTripUseCase, CreateItinerariesUseCase, GetTripUseCase, UpdatePeriodUseCase {
    private final TripRepository tripRepository;
    private final TripQueryRepository tripQueryRepository;

    @Override
    public TripCreateResponse createTrip(TripCreateRequest request) {
        Trip trip = tripRepository.save(request.to());
        return TripCreateResponse.of(trip);
    }

    @Override
    public TripCreateResponse createTripWithItineraries(TripCreateRequest request) {
        Trip trip = tripRepository.save(request.toWithItineraries());
        return TripCreateResponse.of(trip);
    }

    @Override
    public ItinerariesCreateResponse createItineraries(ItinerariesCreateRequest request) {
        Trip trip = tripRepository.findById(request.tripId())
                .orElseThrow(EntityNotFoundException::new);
        Itineraries itineraries = new Itineraries(trip, trip.getPeriod(), request.getDates());
        return ItinerariesCreateResponse.of(trip.getId(), itineraries);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TripResponse> getTrips(Pageable page) {
        return tripQueryRepository.findTrips(page);
    }

    @Override
    public PeriodUpdateResponse updatePeriodUseCase(UUID tripId, PeriodUpdateRequest request) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(EntityNotFoundException::new);
        trip.updatePeriodWithItineraries(request.toPeriod(), request.getDates(), request.updateId());
        return PeriodUpdateResponse.of(trip.getId(), trip.getPeriod(), trip.getItineraries());
    }
}
