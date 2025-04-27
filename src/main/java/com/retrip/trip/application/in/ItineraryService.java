package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;
import com.retrip.trip.application.in.usecase.ManageItinerariesUseCase;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.entity.Trip;
import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ItineraryService implements ManageItinerariesUseCase {
  private final TripRepository tripRepository;

  @Override
  public ItinerariesCreateResponse createItineraries(
      UUID tripId, ItinerariesCreateRequest request) {
    Trip trip = tripRepository.findById(tripId).orElseThrow(EntityNotFoundException::new);
    Itineraries itineraries = new Itineraries(trip, trip.getPeriod(), request.getDates());
    return ItinerariesCreateResponse.of(trip.getId(), itineraries);
  }
}
