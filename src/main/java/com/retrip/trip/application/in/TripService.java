package com.retrip.trip.application.in;

import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_MEMBER_NOT_IN_TRIP;

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
import com.retrip.trip.domain.entity.TripParticipants;
import com.retrip.trip.domain.exception.TripNotFoundException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.vo.TripPeriod;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

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
        TripDemand demand = TripDemand.create(request.memberId(), trip, request.message());
        trip.addDemand(demand);
        return TripDemandResponse.of(demand);
    }

    private Trip findTrip(UUID tripId) {
        return tripRepository.findById(tripId).orElseThrow(TripNotFoundException::new);
    }

    @Override
    public TripDemandApproveResponse approve(UUID memberId, UUID tripId, UUID tripDemandId) {
        TripDemand tripDemand = findTripDemandByTripIdAndTripDemandId(tripId, tripDemandId);
        tripDemand.approve(memberId);
        return TripDemandApproveResponse.of(tripDemand);
    }

    @Override
    public TripDemandRejectResponse reject(UUID memberId, UUID tripId, UUID joinRequestId) {
        TripDemand tripDemand = findTripDemandByTripIdAndTripDemandId(tripId, joinRequestId);
        tripDemand.reject(memberId);
        return TripDemandRejectResponse.of(tripDemand);
    }

    private TripDemand findTripDemandByTripIdAndTripDemandId(UUID tripId, UUID tripDemandId) {
        return tripDemandReadRepository
                .findByTripIdAndId(tripId, tripDemandId)
                .orElseThrow(() -> new EntityNotFoundException("참여 요청을 찾을 수 없습니다."));
    }

    @Override
    public PeriodUpdateResponse updatePeriod(UUID tripId, PeriodUpdateRequest request) {
        TripPeriod period = request.toPeriod();
        Trip trip = tripQueryRepository.findByIdWithItineraries(tripId).orElseThrow(TripNotFoundException::new);
        List<Itinerary> itineraries = tripItineraryQueryRepository.findByIdsWithItineraryDetails(trip.getItinerariesIds());
        trip.updatePeriod(period, request.memberId());
        return PeriodUpdateResponse.of(trip);
    }

    @Override
    public void banMembers(UUID loginMemberId, UUID tripId, List<UUID> memberIds) {
        Trip trip = findTrip(tripId);
        trip.banMembers(loginMemberId, memberIds);
    }
}
