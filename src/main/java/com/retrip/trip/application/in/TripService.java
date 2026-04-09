package com.retrip.trip.application.in;

import static com.retrip.trip.domain.exception.common.ErrorCode.PRIVATE_TRIP_PASSWORD_REQUIRED;

import com.retrip.trip.application.in.request.DelegateLeaderRequest;
import com.retrip.trip.application.in.request.PeriodUpdateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripUpdateRequest;
import com.retrip.trip.application.in.request.TripUpdateVisibilityRequest;
import com.retrip.trip.application.in.response.DelegateLeaderResponse;
import com.retrip.trip.application.in.response.MyTripResponse;
import com.retrip.trip.application.in.response.PeriodUpdateResponse;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.response.TripDetailResponse;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.application.in.response.TripUpdateResponse;
import com.retrip.trip.application.in.response.TripUpdateVisibilityResponse;
import com.retrip.trip.application.in.usecase.DelegateLeaderUseCase;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
import com.retrip.trip.application.in.usecase.LeaveTripUseCase;
import com.retrip.trip.application.in.usecase.TripConfirmationUseCase;
import com.retrip.trip.application.in.usecase.TripManageUseCase;
import com.retrip.trip.application.in.usecase.TripPeriodUseCase;
import com.retrip.trip.application.out.crypto.TripPasswordEncoder;
import com.retrip.trip.application.out.gateway.MemberGateway;
import com.retrip.trip.application.out.repository.DemandRepository;
import com.retrip.trip.application.out.repository.InvitationRepository;
import com.retrip.trip.application.out.repository.TripItineraryQueryRepository;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.application.out.repository.VoteRepository;
import com.retrip.trip.domain.entity.Itinerary;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripDestinations;
import com.retrip.trip.domain.entity.TripHashTag;
import com.retrip.trip.domain.entity.TripHashTags;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.exception.TripNotFoundException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.vo.DemandStatus;
import com.retrip.trip.domain.vo.InvitationStatus;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPassword;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripStatus;
import com.retrip.trip.domain.vo.TripTitle;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Transactional
@Service
public class TripService
        implements TripManageUseCase, GetTripUseCase, TripPeriodUseCase, LeaveTripUseCase, DelegateLeaderUseCase, TripConfirmationUseCase {
    private final TripRepository tripRepository;
    private final TripQueryRepository tripQueryRepository;
    private final TripItineraryQueryRepository tripItineraryQueryRepository;
//    private final TripConfirmationDemandRepository tripConfirmationDemandRepository;
    private final TripPasswordEncoder tripPasswordEncoder;
    private final MemberGateway memberGateway;
    private final DemandRepository demandRepository;
    private final InvitationRepository invitationRepository;
    private final VoteRepository voteRepository;

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
    public TripUpdateVisibilityResponse updateTripVisibility(UUID memberId, UUID tripId, TripUpdateVisibilityRequest request) {
        Trip trip = findTrip(tripId);
        trip.validateLeader(memberId);
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
        List<UUID> participantMemberIds = trip.getTripParticipants().getValues().stream()
                .map(TripParticipant::getMemberId)
                .toList();
        Map<UUID, MemberGateway.MemberInfo> memberInfoMap = memberGateway.getMembersByIds(participantMemberIds).stream()
                .collect(Collectors.toMap(MemberGateway.MemberInfo::id, m -> m));

        boolean isPendingDemand = demandRepository
                .findByTripIdAndMemberIdAndStatus(tripId, memberId, DemandStatus.PENDING)
                .isPresent();
        UUID pendingInvitationId = invitationRepository
                .findByTripIdAndMemberIdAndStatus(tripId, memberId, InvitationStatus.INVITED)
                .map(Invitation::getId)
                .orElse(null);

        return TripDetailResponse.of(memberId, trip, memberInfoMap, isPendingDemand, pendingInvitationId);
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
    public void deleteTrip(UUID memberId, UUID tripId) {
        Trip trip = findTrip(tripId);
        trip.validateDeletable(memberId);
        invitationRepository.deleteByTripId(tripId);
        demandRepository.deleteByTripId(tripId);
        voteRepository.deleteByTripId(tripId);
        tripRepository.delete(trip);
    }

    @Override
    public void toggleRecruitmentStatus(UUID memberId, UUID tripId) {
        Trip trip = findTrip(tripId);
        trip.validateLeader(memberId);
        trip.toggleRecruitmentStatus();
    }

    @Override
    public void leaveTrip(UUID tripId, UUID memberId) {
        Trip trip = findTrip(tripId);
        trip.leave(memberId);
        demandRepository.deleteByMemberId(memberId);
    }

    @Override
    public DelegateLeaderResponse delegateLeader(UUID tripId, UUID currentLeaderId, DelegateLeaderRequest request) {
        Trip trip = findTrip(tripId);
        trip.delegateLeader(currentLeaderId, request.newLeaderId());
        return DelegateLeaderResponse.of(trip, request.newLeaderId());
    }

//    @Override
//    public void demandTripConfirmation(UUID loginMemberId, UUID tripId, TripConfirmationDemandRequest request) {
//        Trip trip = findTrip(tripId);
//
//        TripConfirmationDemand confirmationDemand = TripConfirmationDemand.create(loginMemberId, trip, request.startDate(), request.endDate());
//        confirmationDemand.addTripMember(loginMemberId);
//
//        tripConfirmationDemandRepository.save(confirmationDemand);
        //TODO: 알림 보내야함
//    }

//    @Override
//    public void demandAgainTripConfirmation(UUID loginMemberId, UUID tripId, UUID confirmationDemandId, TripConfirmationDemandRequest request) {
//        TripConfirmationDemand savedDemand = findTripConfirmationDemandById(confirmationDemandId);
//        savedDemand.demandAgain(loginMemberId, request.startDate(), request.endDate());
//        TODO: 알림 보내야함
//    }

//    @Override
//    public ConfirmationDemandAcceptResponse acceptConfirmationDemand(UUID loginMemberId, UUID tripId, UUID confirmationDemandId) {
//        TripConfirmationDemand demand = findTripConfirmationDemandById(confirmationDemandId);
//        demand.accept(loginMemberId);
        //TODO: 여행 방장에게 알림 보내야함

//        return ConfirmationDemandAcceptResponse.of(demand.getTrip());
//    }

//    @Override
//    public void rejectConfirmationDemand(UUID loginMemberId, UUID tripId, UUID confirmationDemandId) {
//        TripConfirmationDemand demand = findTripConfirmationDemandById(confirmationDemandId);
//        demand.reject(loginMemberId);
//        TODO: 여행 방장에게 알림 보내야함
//    }

//    private TripConfirmationDemand findTripConfirmationDemandById(UUID confirmationDemandId) {
//        return tripConfirmationDemandRepository.findById(confirmationDemandId)
//                .orElseThrow(() -> new BusinessException(PARTICIPATION_CONFIRM_REQUEST_NOT_FOUND));
//    }

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
