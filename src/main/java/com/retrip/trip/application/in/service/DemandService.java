package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.request.context.UserContext;
import com.retrip.trip.application.in.request.demand.TripDemandRequest;
import com.retrip.trip.application.in.response.demand.DemandApproveResponse;
import com.retrip.trip.application.in.response.demand.DemandRejectResponse;
import com.retrip.trip.application.in.response.demand.DemandResponse;
import com.retrip.trip.application.in.response.demand.DemandsResponse;
import com.retrip.trip.application.in.usecase.DemandManageUseCase;
import com.retrip.trip.application.out.client.AlarmApiClient;
import com.retrip.trip.application.out.client.model.CallAlarmType;
import com.retrip.trip.application.out.repository.DemandRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.entity.demand.Demand;
import com.retrip.trip.domain.exception.TripNotFoundException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.EntityNotFoundException;
import com.retrip.trip.domain.service.DemandPolicy;
import com.retrip.trip.infra.adapter.out.client.webclient.api.response.CreateAlarmsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.CANNOT_FIND_LEADER;
import static com.retrip.trip.domain.exception.common.ErrorCode.DEMAND_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class DemandService implements DemandManageUseCase {
    private final TripRepository tripRepository;
    private final DemandRepository demandRepository;
    private final DemandPolicy demandPolicy;
    private final AlarmApiClient alarmApiClient;

    @Override
    public DemandResponse demand(UserContext context, UUID tripId, TripDemandRequest request) {
        Trip trip = findTrip(tripId);
        List<Demand> savedDemands = demandRepository.findAllByTripId(tripId);
        demandPolicy.canDemand(context.memberId(), trip, savedDemands);
        Demand savedDemand = demandRepository.save(Demand.create(context.memberId(), tripId, request.message()));
        TripParticipant leader = trip.getLeader()
                .orElseThrow(() -> new EntityNotFoundException(CANNOT_FIND_LEADER));

        Map<String, Object> parameters = Map.of(
                "senderName", context.nickName(),
                "tripName", trip.getTitle()
        );

        alarmApiClient.sendAlarms(context.memberId(), List.of(leader.getMemberId()), parameters, CallAlarmType.DEMAND);

        return DemandResponse.of(savedDemand.getId(), savedDemand.getTripId(), savedDemand.getMemberId(), savedDemand.getMessage(), savedDemand.getStatus());
    }

    @Override
    public DemandApproveResponse approve(UUID memberId, UUID tripId, UUID demandId) {
        Trip trip = findTrip(tripId);
        Demand demand = findDemand(demandId);
        demandPolicy.canApprove(memberId, trip, demand);
        demand.approve();
        trip.addParticipant(TripParticipant.createTripParticipant(demand.getMemberId(), trip));

        //TODO : 참여요청한 사용자에게 승인 알림 보내기
        return DemandApproveResponse.of(demand.getMemberId(), demand.getStatus());
    }

    @Override
    public DemandRejectResponse reject(UUID memberId, UUID tripId, UUID demandId) {
        Trip trip = findTrip(tripId);
        Demand demand = findDemand(demandId);
        demandPolicy.canReject(memberId, trip, demand);
        demand.reject();

        //TODO : 참여요청한 사용자에게 거절 알림 보내기
        return DemandRejectResponse.of(demand.getMemberId(), demand.getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandsResponse> getDemands(UUID memberId, UUID tripId) {
        Trip trip = findTrip(tripId);
        demandPolicy.canViewDemands(memberId, trip);
        List<Demand> demands = demandRepository.findAllByTripId(tripId);
        return demands.stream()
                .map(DemandsResponse::of)
                .toList();
    }

    private Trip findTrip(UUID tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(TripNotFoundException::new);
    }

    private Demand findDemand(UUID demandId) {
        return demandRepository.findById(demandId)
                .orElseThrow(() -> new BusinessException(DEMAND_NOT_FOUND));
    }
}
