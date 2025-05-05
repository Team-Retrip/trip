package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripDemandRequest;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.usecase.CreateItinerariesUseCase;
import com.retrip.trip.application.in.usecase.CreateTripUseCase;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
import com.retrip.trip.application.in.usecase.TripDemandUseCase;
import com.retrip.trip.application.out.repository.TripDemandRepository;
import com.retrip.trip.application.out.repository.TripParticipantRepository;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripDemand;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.exception.TripNotFoundException;
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
public class TripService implements CreateTripUseCase, CreateItinerariesUseCase, GetTripUseCase, TripDemandUseCase {
    private final TripRepository tripRepository;
    private final TripQueryRepository tripQueryRepository;
    private final TripDemandRepository tripDemandRepository;
    private final TripParticipantRepository tripParticipantRepository;

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
    public TripDemandResponse tripDemand(UUID tripId, TripDemandRequest request) {
        Trip trip = findTrip(tripId);
        trip.validateTripRecruitingStatus();
        TripDemand demand = TripDemand.create(request.memberId(), trip, request.message());
        trip.addDemand(demand);
        return TripDemandResponse.of(demand);
    }

    private Trip findTrip(UUID tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(TripNotFoundException::new);
    }

    public TripDemandApproveResponse approve(UUID tripId, UUID joinRequestId) {
        TripDemand tripDemand = findJoinRequestBy(tripId, joinRequestId);
        tripDemand.approve();
        addApprovedParticipant(tripDemand);
        return new TripDemandApproveResponse(tripDemand.getStatus().getCode());
    }

    private void addApprovedParticipant(TripDemand tripDemand) {
        TripParticipant participant = TripParticipant.createTripParticipant(tripDemand.getMemberId(), tripDemand.getTrip());
        tripParticipantRepository.save(participant);
    }

    private TripDemand findJoinRequestBy(UUID tripId, UUID joinRequestId) {
        return tripDemandRepository.findByTripIdAndId(tripId, joinRequestId)
                .orElseThrow(() -> new EntityNotFoundException("참여 요청을 찾을 수 없습니다."));
    }

    public TripDemandRejectResponse reject(UUID tripId, UUID joinRequestId) {
        TripDemand tripDemand = findJoinRequestBy(tripId, joinRequestId);
        tripDemand.reject();
        return new TripDemandRejectResponse(tripDemand.getStatus().getCode());
    }
}
