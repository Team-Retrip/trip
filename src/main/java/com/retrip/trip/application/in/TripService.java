package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.DelegateLeaderRequest;
import com.retrip.trip.application.in.request.PeriodUpdateRequest;
import com.retrip.trip.application.in.request.TripConfirmationDemandRequest;
import com.retrip.trip.application.in.request.TripConfirmationDemandRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripDemandRequest;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.usecase.*;
import com.retrip.trip.application.out.repository.*;
import com.retrip.trip.application.in.response.ConfirmationDemandAcceptResponse;
import com.retrip.trip.application.in.response.PeriodUpdateResponse;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.response.TripDemandApproveResponse;
import com.retrip.trip.application.in.response.TripDemandRejectResponse;
import com.retrip.trip.application.in.response.TripDemandResponse;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.application.in.usecase.CreateTripUseCase;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
import com.retrip.trip.application.in.usecase.TripConfirmationUseCase;
import com.retrip.trip.application.in.usecase.TripDemandUseCase;
import com.retrip.trip.application.in.usecase.TripPeriodUseCase;
import com.retrip.trip.application.out.repository.TripConfirmationDemandRepository;
import com.retrip.trip.application.out.repository.TripDemandReadRepository;
import com.retrip.trip.application.out.repository.TripItineraryQueryRepository;
import com.retrip.trip.application.out.repository.TripParticipantRepository;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripConfirmationDemand;
import com.retrip.trip.domain.entity.TripConfirmationDemand;
import com.retrip.trip.domain.entity.TripDemand;
import com.retrip.trip.domain.exception.TripNotFoundException;
import com.retrip.trip.domain.vo.TripPeriod;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class TripService
        implements CreateTripUseCase, GetTripUseCase, TripDemandUseCase, TripPeriodUseCase, LeaveTripUseCase, DelegateLeaderUseCase, TripConfirmationUseCase {
    private final TripRepository tripRepository;
    private final TripQueryRepository tripQueryRepository;
    private final TripItineraryQueryRepository tripItineraryQueryRepository;
    private final TripDemandReadRepository tripDemandReadRepository;
    private final TripConfirmationDemandRepository tripConfirmationDemandRepository;

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
        trip.addDemand(TripDemand.create(request.memberId(), trip, request.message()));
        tripRepository.save(trip);
        return TripDemandResponse.of(trip.getTripDemands().getValues().getLast());
    }

    private Trip findTrip(UUID tripId) {
        return tripRepository.findWithParticipantsById(tripId).orElseThrow(TripNotFoundException::new);
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

    @Transactional(readOnly = true)
    @Override
    public Page<TripResponse> getMyTrips(UUID memberId, Pageable page) {
        return tripQueryRepository.findMyTrips(memberId, page);
    }

    @Override
    public void banMembers(UUID loginMemberId, UUID tripId, List<UUID> memberIds) {
        Trip trip = findTrip(tripId);
        trip.banMembers(loginMemberId, memberIds);
    }

    @Override
    public void leaveTrip(UUID tripId, UUID memberId) {
        Trip trip = findTrip(tripId);
        trip.leave(memberId);
        tripRepository.save(trip);
    }

    @Override
    public DelegateLeaderResponse delegateLeader(UUID tripId, DelegateLeaderRequest request) {
        Trip trip = findTrip(tripId);
        trip.delegateLeader(request.currentLeaderId(), request.newLeaderId());
        return DelegateLeaderResponse.of(trip, request.newLeaderId());
    }

    @Override
    public void demandTripConfirmation(UUID loginMemberId, UUID tripId, TripConfirmationDemandRequest request) {
        Trip trip = findTrip(tripId);

        TripConfirmationDemand confirmationDemand = TripConfirmationDemand.create(loginMemberId, trip, request.startDate(), request.endDate());
        confirmationDemand.addTripMember(loginMemberId);

        tripConfirmationDemandRepository.save(confirmationDemand);
        //TODO: 알림 보내야함
    }

    @Override
    public void demandAgainTripConfirmation(UUID loginMemberId, UUID tripId, UUID confirmationDemandId, TripConfirmationDemandRequest request) {
        TripConfirmationDemand savedDemand = findTripConfirmationDemandById(confirmationDemandId);
        savedDemand.demandAgain(loginMemberId, request.startDate(), request.endDate());
        //TODO: 알림 보내야함
    }

    @Override
    public ConfirmationDemandAcceptResponse acceptConfirmationDemand(UUID loginMemberId, UUID tripId, UUID confirmationDemandId) {
        TripConfirmationDemand demand = findTripConfirmationDemandById(confirmationDemandId);
        demand.accept(loginMemberId);
        //TODO: 여행 방장에게 알림 보내야함

        return ConfirmationDemandAcceptResponse.of(demand.getTrip());
    }

    @Override
    public void rejectConfirmationDemand(UUID loginMemberId, UUID tripId, UUID confirmationDemandId) {
        TripConfirmationDemand demand = findTripConfirmationDemandById(confirmationDemandId);
        demand.reject(loginMemberId);
        //TODO: 여행 방장에게 알림 보내야함
    }

    private TripConfirmationDemand findTripConfirmationDemandById(UUID confirmationDemandId) {
        return tripConfirmationDemandRepository.findById(confirmationDemandId)
                .orElseThrow(() -> new EntityNotFoundException("참여 확정 요청을 찾을 수 없습니다."));
    }
}