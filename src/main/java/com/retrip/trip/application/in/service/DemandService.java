package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.request.demand.TripDemandRequest;
import com.retrip.trip.application.in.response.demand.DemandApproveResponse;
import com.retrip.trip.application.in.response.demand.DemandRejectResponse;
import com.retrip.trip.application.in.response.demand.DemandResponse;
import com.retrip.trip.application.in.response.demand.DemandsResponse;
import com.retrip.trip.application.in.usecase.DemandManageUseCase;
import com.retrip.trip.application.out.repository.DemandRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.entity.demand.Demand;
import com.retrip.trip.domain.exception.common.EntityNotFoundException;
import com.retrip.trip.domain.service.DemandPolicy;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DemandService implements DemandManageUseCase {
    private final TripRepository tripRepository;
    private final DemandRepository demandRepository;
    private final DemandPolicy demandPolicy;

    @Override
    public DemandResponse demand(UUID memberId, UUID tripId, TripDemandRequest request) {
        Trip trip = findTripWithParticipants(tripId);
        List<Demand> savedDemands = demandRepository.findAllByTripId(tripId);
        demandPolicy.canDemand(memberId, trip, savedDemands);
        Demand savedDemand = demandRepository.save(Demand.create(memberId, tripId, request.message()));

        //TODO : 리더에게 참여요청 알림 보내기
        return DemandResponse.of(savedDemand.getId(), savedDemand.getTripId(), savedDemand.getMemberId(), savedDemand.getMessage(), savedDemand.getStatus());
    }

    @Override
    public DemandApproveResponse approve(UUID memberId, UUID tripId, UUID demandId) {
        Trip trip = findTripWithParticipants(tripId);
        Demand demand = findById(demandId);
        demandPolicy.canApprove(memberId, trip, demand);
        demand.approve();
        trip.addParticipant(TripParticipant.createTripParticipant(demand.getMemberId(), trip));

        //TODO : 참여요청한 사용자에게 승인 알림 보내기
        return DemandApproveResponse.of(demand.getMemberId(), demand.getStatus());
    }

    @Override
    public DemandRejectResponse reject(UUID memberId, UUID tripId, UUID demandId) {
        Trip trip = findTripWithParticipants(tripId);
        Demand demand = findById(demandId);
        demandPolicy.canReject(memberId, trip, demand);
        demand.reject();

        //TODO : 참여요청한 사용자에게 거절 알림 보내기
        return DemandRejectResponse.of(demand.getMemberId(), demand.getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandsResponse> getDemands(UUID memberId, UUID tripId) {
        Trip trip = findTripWithParticipants(tripId);
        demandPolicy.canViewDemands(memberId, trip);
        List<Demand> demands = demandRepository.findAllByTripId(tripId);
        return demands.stream()
                .map(DemandsResponse::of)
                .toList();
    }

    private Trip findTripWithParticipants(UUID tripId) {
        return tripRepository.findWithParticipantsById(tripId)
                .orElseThrow(EntityNotFoundException::new);
    }

    private Demand findById(UUID demandId) {
        return demandRepository.findById(demandId)
                .orElseThrow(EntityNotFoundException::new);
    }
}
