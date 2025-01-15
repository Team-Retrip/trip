package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.usecase.CreateTripUseCase;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class TripService implements CreateTripUseCase {
    private final TripRepository tripRepository;

    @Override
    public TripCreateResponse createTrip(TripCreateRequest request) {
        Trip trip = tripRepository.save(request.to());
        trip.registerLeader(request.memberId());
        return TripCreateResponse.of(trip);
    }
}
