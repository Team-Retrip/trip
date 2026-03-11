package com.retrip.trip.application.in;

import com.retrip.trip.application.in.request.*;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.usecase.*;
import com.retrip.trip.application.out.crypto.TripPasswordEncoder;
import com.retrip.trip.application.out.repository.TripConfirmationDemandRepository;
import com.retrip.trip.application.out.repository.TripItineraryQueryRepository;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.*;
import com.retrip.trip.domain.exception.TripNotFoundException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.PARTICIPATION_CONFIRM_REQUEST_NOT_FOUND;
import static com.retrip.trip.domain.exception.common.ErrorCode.PRIVATE_TRIP_PASSWORD_REQUIRED;

@RequiredArgsConstructor
@Transactional
@Service
public class TripService
        implements TripManageUseCase, GetTripUseCase, TripPeriodUseCase, LeaveTripUseCase, DelegateLeaderUseCase, TripConfirmationUseCase {
    private final TripRepository tripRepository;
    private final TripQueryRepository tripQueryRepository;
    private final TripItineraryQueryRepository tripItineraryQueryRepository;
    private final TripConfirmationDemandRepository tripConfirmationDemandRepository;
    private final TripPasswordEncoder tripPasswordEncoder;

    @Override
    public TripCreateResponse createTrip(UUID memberId, TripCreateRequest request) {
        Trip trip = request.to(memberId);
        assignPasswordIfNotOpen(trip, request.password());
        Trip savedTrip = tripRepository.save(trip);
        return TripCreateResponse.of(savedTrip);
    }

    @Override
    public TripCreateResponse createTripWithItineraries(UUID memberId, TripCreateRequest request) {
        Trip trip = request.toWithItineraries(memberId);
        assignPasswordIfNotOpen(trip, request.password());
        Trip savedTrip = tripRepository.save(trip);
        return TripCreateResponse.of(savedTrip);
    }

    @Override
    public TripUpdateResponse updateTrip(UUID memberId, UUID tripId, TripUpdateRequest request) {
        Trip trip = findTrip(tripId);

        TripTitle tripTitle = request.toTripTitle();
        TripDestinations destinations = request.toTripDestinations(trip);
        TripDescription tripDescription = request.toTripDescription();
        TripPeriod tripPeriod = request.toTripPeriod();
        TripHashTags hashTags = request.toHashTags(trip);

        //List<Itinerary> itineraries = tripItineraryQueryRepository.findByIdsWithItineraryDetails(trip.getItinerariesIds());
        trip.update(
                memberId,
                destinations,
                tripTitle,
                tripDescription,
                hashTags,
                request.maxParticipants(),
                request.imageUrl(),
                request.category()
        );
        trip.updatePeriod(tripPeriod, memberId);

        return TripUpdateResponse.of(trip);
    }

    @Override
    public TripUpdateVisibilityResponse updateTripVisibility(UUID tripId, TripUpdateVisibilityRequest request) {
        Trip trip = findTrip(tripId);
        assignPasswordIfNotOpen(trip, request.password());
        trip.updateVisibility(request.open());
        return TripUpdateVisibilityResponse.of(trip, request.password());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TripResponse> getTrips(List<TripStatus> tripStatuses, List<String> genders, List<String> ages, Pageable page) {
        Page<Trip> tripsPage = tripQueryRepository.findTrips(tripStatuses, genders, ages, page);
        List<Trip> trips = tripsPage.getContent();
        List<TripHashTag> hashTags = tripQueryRepository.findHashTags(trips);

        return new PageImpl<>(TripResponse.of(trips, hashTags), page, tripsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public TripDetailResponse getTripDetail(UUID memberId, UUID tripId) {
        Trip trip = findTrip(tripId);
        //TODO: 해당 참가자 정보 auth API 에서 따로 가져오도록 수정해야함
        return TripDetailResponse.of(memberId, trip);
    }

    private Trip findTrip(UUID tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(TripNotFoundException::new);
    }

    @Override
    public PeriodUpdateResponse updatePeriod(UUID memberId, UUID tripId, PeriodUpdateRequest request) {
        TripPeriod period = request.toPeriod();
        Trip trip = tripQueryRepository.findByIdWithItineraries(tripId).orElseThrow(TripNotFoundException::new);
        List<Itinerary> itineraries = tripItineraryQueryRepository.findByIdsWithItineraryDetails(trip.getItinerariesIds());
        trip.updatePeriod(period, memberId);
        return PeriodUpdateResponse.of(trip);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MyTripResponse> getMyTrips(UUID memberId, List<TripStatus> tripStatuses, List<String> genders, List<String> ages, Pageable page) {
        return tripQueryRepository.findMyTrips(memberId, tripStatuses, genders, ages, page);
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
                .orElseThrow(() -> new BusinessException(PARTICIPATION_CONFIRM_REQUEST_NOT_FOUND));
    }

    private void assignPasswordIfNotOpen(Trip trip, String password) {
        if (trip.isOpen()) {
            return;
        }
        String trimPassword = password.trim();
        if (!StringUtils.hasText(trimPassword)) {
            throw new InvalidValueException(PRIVATE_TRIP_PASSWORD_REQUIRED);
        }
        String passwordHash = tripPasswordEncoder.encode(trimPassword);
        TripPassword tripPassword = new TripPassword(trimPassword, passwordHash);
        trip.assignPassword(tripPassword);
    }
}
