package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.PeriodUpdateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripDemandRequest;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.usecase.CreateTripUseCase;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
import com.retrip.trip.application.in.usecase.TripDemandUseCase;
import com.retrip.trip.application.in.usecase.TripPeriodUseCase;
import com.retrip.trip.application.out.repository.*;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripDemand;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.exception.TripNotFoundException;
import com.retrip.trip.domain.vo.TripPeriod;
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
public class TripService
        implements CreateTripUseCase, GetTripUseCase, TripDemandUseCase, TripPeriodUseCase {
    private final TripRepository tripRepository;
    private final TripQueryRepository tripQueryRepository;
    private final TripItineraryQueryRepository tripItineraryQueryRepository;
    private final TripDemandReadRepository tripDemandReadRepository;
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
        tripRepository.save(trip);
        return TripDemandResponse.of(demand);
    }

    private Trip findTrip(UUID tripId) {
        return tripRepository.findById(tripId).orElseThrow(TripNotFoundException::new);
    }

    public TripDemandApproveResponse approve(UUID tripId, UUID joinRequestId) {
        TripDemand tripDemand = findJoinRequestBy(tripId, joinRequestId);
        tripDemand.approve();
        addApprovedParticipant(tripDemand);
        return new TripDemandApproveResponse(tripDemand.getStatus().getCode());
    }

    private void addApprovedParticipant(TripDemand tripDemand) {
        TripParticipant participant =
                TripParticipant.createTripParticipant(tripDemand.getMemberId(), tripDemand.getTrip());
        tripParticipantRepository.save(participant);
    }

    private TripDemand findJoinRequestBy(UUID tripId, UUID joinRequestId) {
        return tripDemandReadRepository
                .findByTripIdAndId(tripId, joinRequestId)
                .orElseThrow(() -> new EntityNotFoundException("참여 요청을 찾을 수 없습니다."));
    }

    public TripDemandRejectResponse reject(UUID tripId, UUID joinRequestId) {
        TripDemand tripDemand = findJoinRequestBy(tripId, joinRequestId);
        tripDemand.reject();
        return new TripDemandRejectResponse(tripDemand.getStatus().getCode());
    }

    @Override
    public PeriodUpdateResponse updatePeriod(UUID tripId, PeriodUpdateRequest request) {
        TripPeriod period = request.toPeriod();
        Trip trip = tripQueryRepository.findByIdWithItineraries(tripId).orElseThrow(TripNotFoundException::new);
        List<Itinerary> itineraries = tripItineraryQueryRepository.findByIdsWithItineraryDetails(trip.getItinerariesIds());
        trip.updatePeriod(period, request.memberId());
        return PeriodUpdateResponse.of(trip);
    }
}
