package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.request.*;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.usecase.*;
import com.retrip.trip.application.out.repository.*;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripDemand;
import com.retrip.trip.domain.entity.participant.Participant;
import com.retrip.trip.domain.exception.TripNotFoundException;
import com.retrip.trip.domain.vo.TripPeriod;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class TripService
        implements CreateTripUseCase,
                GetTripUseCase,
                TripDemandUseCase,
                TripPeriodUseCase,
                LeaveTripUseCase,
                DelegateLeaderUseCase,
                TripManagementUseCase {
    private final TripRepository tripRepository;
    private final TripQueryRepository tripQueryRepository;
    private final TripItineraryQueryRepository tripItineraryQueryRepository;
    private final TripDemandReadRepository tripDemandReadRepository;
    private final ParticipantService participantService;

    @Override
    public TripCreateResponse createTrip(TripCreateRequest request) {
        Trip trip = tripRepository.save(request.to());
        Participant participant =
                participantService.createLeaderParticipant(trip.getId(), request.memberId());
        return TripCreateResponse.of(trip, participant);
    }

    @Override
    public TripCreateResponse createTripWithItineraries(TripCreateRequest request) {
        Trip trip = tripRepository.save(request.toWithItineraries());
        Participant participant =
                participantService.createLeaderParticipant(trip.getId(), request.memberId());
        return TripCreateResponse.of(trip, participant);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TripResponse> getTrips(Pageable page) {
        return tripQueryRepository.findTrips(page);
    }

    @Override
    public TripDemandResponse tripDemand(UUID tripId, TripDemandRequest request) {
        Trip trip = findTrip(tripId);
        participantService.canDemand(tripId, request.memberId(), trip.getMaxParticipants());
        trip.addDemand(TripDemand.create(request.memberId(), trip, request.message()));
        return TripDemandResponse.of(trip.getTripDemands().getValues().getLast());
    }

    private Trip findTrip(UUID tripId) {
        return tripRepository.findById(tripId).orElseThrow(TripNotFoundException::new);
    }

    @Override
    public TripDemandApproveResponse approve(
            UUID memberId, UUID approverMemberId, UUID tripId, UUID tripDemandId) {
        TripDemand tripDemand = findTripDemandByTripIdAndTripDemandId(tripId, tripDemandId);
        participantService.requireLeaderOrElseThrow(tripId, memberId);
        tripDemand.approve();
        participantService.createParticipant(
                tripId, approverMemberId, tripDemand.getTrip().getMaxParticipants());
        return TripDemandApproveResponse.of(tripDemand);
    }

    @Override
    public TripDemandRejectResponse reject(UUID memberId, UUID tripId, UUID joinRequestId) {
        TripDemand tripDemand = findTripDemandByTripIdAndTripDemandId(tripId, joinRequestId);
        participantService.requireLeaderOrElseThrow(tripId, memberId);
        tripDemand.reject();
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
        Trip trip =
                tripQueryRepository
                        .findByIdWithItineraries(tripId)
                        .orElseThrow(TripNotFoundException::new);
        List<Itinerary> itineraries =
                tripItineraryQueryRepository.findByIdsWithItineraryDetails(
                        trip.getItinerariesIds());
        participantService.requireLeaderOrElseThrow(tripId, request.memberId());
        trip.updatePeriod(period, request.memberId());
        return PeriodUpdateResponse.of(trip);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MyTripResponse> getMyTrips(UUID memberId, Pageable page) {
        List<Participant> participants = participantService.findByMemberId(memberId, page);
        Long total = participantService.findByMemberIdTotalCount(memberId);
        List<UUID> tripIds = participants.stream().map(Participant::getTripId).toList();

        List<MyTripResponse> trips = tripQueryRepository.findMyTrips(tripIds);
        return new PageImpl<>(trips, page, total == null ? 0 : total);
    }

    @Override
    public void banMembers(UUID loginMemberId, UUID tripId, List<UUID> memberIds) {
        Trip trip = findTrip(tripId);
        trip.canBan();
        List<Participant> participants =
                participantService.banMembers(tripId, loginMemberId, memberIds);
    }

    @Override
    public void leaveTrip(UUID tripId, UUID memberId) {
        Trip trip = findTrip(tripId);
        trip.canLeave();
        participantService.requireParticipantOrElseThrow(tripId, memberId);
        participantService.remove(tripId, memberId);
    }

    @Override
    public DelegateLeaderResponse delegateLeader(UUID tripId, DelegateLeaderRequest request) {
        Trip trip = findTrip(tripId);
        trip.canDelegateLeader();
        Participant participant =
                participantService.delegateLeader(
                        tripId, request.currentLeaderId(), request.newLeaderId());
        return DelegateLeaderResponse.of(trip, participant);
    }

    @Override
    public MaxParticipantUpdateResponse updateMaxParticipants(
            UUID tripId, MaxParticipantUpdateRequest request) {
        Trip trip = findTrip(tripId);
        participantService.canUpdateMaxParticipant(
                tripId, request.memberId(), request.maxParticipants());
        trip.updateMaxParticipants(request.maxParticipants());
        return MaxParticipantUpdateResponse.of(trip.getId(), trip.getMaxParticipants());
    }
}
