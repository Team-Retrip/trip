package com.retrip.trip.application.in;
import java.util.UUID;

import com.retrip.trip.application.in.request.ItinerariesCreateRequest;
import com.retrip.trip.application.in.request.TripCreateRequest;
import com.retrip.trip.application.in.request.TripJoinApplyRequest;
import com.retrip.trip.application.in.response.ItinerariesCreateResponse;
import com.retrip.trip.application.in.response.TripCreateResponse;
import com.retrip.trip.application.in.response.TripJoinResponse;
import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.application.in.usecase.CreateItinerariesUseCase;
import com.retrip.trip.application.in.usecase.CreateTripUseCase;
import com.retrip.trip.application.in.usecase.GetTripUseCase;
import com.retrip.trip.application.in.usecase.JoinApplyUseCase;
import com.retrip.trip.application.out.repository.TripQueryRepository;
import com.retrip.trip.application.out.repository.JoinRequestRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Itineraries;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.entity.JoinRequest;
import com.retrip.trip.domain.vo.ParticipantStatus;
import com.retrip.trip.domain.vo.TripStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class TripService implements CreateTripUseCase, CreateItinerariesUseCase, GetTripUseCase, JoinApplyUseCase {
    private final TripRepository tripRepository;
    private final TripQueryRepository tripQueryRepository;
    private final JoinRequestRepository joinRequestRepository;

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
    public TripJoinResponse JoinApply(TripJoinApplyRequest request) {
        Trip trip = findTrip(request.tripId());
        if (!trip.getStatus().equals(TripStatus.RECRUITING)) {
            throw new IllegalStateException("해당 여행은 모집 중이 아닙니다.");
        }
        JoinRequest joinRequest = request.to(trip);
        joinRequestRepository.save(joinRequest);
        return TripJoinResponse.of(joinRequest);
    }

    private Trip findTrip(UUID tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("여행을 찾을 수 없습니다."));
    }

    private JoinRequest validateJoinRequest(UUID tripId, UUID joinRequestId) {
        Trip trip = findTrip(tripId);
        JoinRequest joinRequest = joinRequestRepository.findById(joinRequestId)
                .orElseThrow(() -> new EntityNotFoundException("참여 요청을 찾을 수 없습니다."));
        if (!joinRequest.getTrip().getId().equals(trip.getId())) {
            throw new IllegalArgumentException("해당 참여 요청은 지정된 여행에 속하지 않습니다.");
        }
        joinRequest.ensurePending();
        return joinRequest;
    }

    public TripParticipant approveJoinRequest(UUID tripId, UUID joinRequestId) {
        JoinRequest joinRequest = validateJoinRequest(tripId, joinRequestId);
        return joinRequest.approve();
    }

    public TripJoinResponse rejectJoinRequest(UUID tripId, UUID joinRequestId) {
        JoinRequest joinRequest = validateJoinRequest(tripId, joinRequestId);
        joinRequest.reject();
        return TripJoinResponse.of(joinRequest);
    }
}
