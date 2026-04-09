package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.request.demand.TripDemandRequest;
import com.retrip.trip.application.in.response.MyPageDemandResponse;
import com.retrip.trip.application.in.response.demand.DemandApproveResponse;
import com.retrip.trip.application.in.response.demand.DemandRejectResponse;
import com.retrip.trip.application.in.response.demand.DemandResponse;
import com.retrip.trip.application.in.response.demand.DemandsResponse;
import com.retrip.trip.application.in.usecase.DemandManageUseCase;
import com.retrip.trip.application.out.gateway.AlarmGateway;
import com.retrip.trip.application.out.gateway.MemberGateway;
import com.retrip.trip.application.out.repository.DemandRepository;
import com.retrip.trip.application.out.repository.MyPageDemandQueryRepository;
import com.retrip.trip.application.out.repository.TripRepository;
import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import com.retrip.trip.domain.entity.demand.Demand;
import com.retrip.trip.domain.exception.TripNotFoundException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.EntityNotFoundException;
import com.retrip.trip.domain.service.DemandPolicy;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.retrip.trip.domain.exception.common.ErrorCode.CANNOT_FIND_LEADER;
import static com.retrip.trip.domain.exception.common.ErrorCode.DEMAND_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class DemandService implements DemandManageUseCase {
    private final TripRepository tripRepository;
    private final DemandRepository demandRepository;
    private final MyPageDemandQueryRepository myPageDemandQueryRepository;
    private final DemandPolicy demandPolicy;
    private final AlarmGateway alarmGateway;
    private final MemberGateway memberGateway;

    @Override
    public DemandResponse demand(UUID memberId, String nickName, UUID tripId, TripDemandRequest request) {
        Trip trip = findTrip(tripId);
        List<Demand> savedDemands = demandRepository.findAllByTripId(tripId);
        demandPolicy.canDemand(memberId, trip, savedDemands);
        Demand savedDemand = demandRepository.save(Demand.create(memberId, tripId, request.message()));
        TripParticipant leader = trip.getLeader()
                .orElseThrow(() -> new EntityNotFoundException(CANNOT_FIND_LEADER));

//        Map<String, Object> parameters = Map.of(
//                "senderName", nickName,
//                "tripName", trip.getTitle()
//        );

//        alarmGateway.sendAlarms(memberId, List.of(leader.getMemberId()), parameters, CallAlarmType.DEMAND);

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
        List<UUID> demandMemberIds = demands.stream().map(Demand::getMemberId).distinct().toList();
        Map<UUID, MemberGateway.MemberInfo> memberInfoMap = memberGateway.getMembersByIds(demandMemberIds).stream()
                .collect(Collectors.toMap(MemberGateway.MemberInfo::id, m -> m));
        return demands.stream()
                .map(d -> DemandsResponse.of(d, memberInfoMap.get(d.getMemberId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MyPageDemandResponse> getMyPageDemands(
            UUID memberId, List<TripStatus> tripStatuses, TripCategory category, String period, Pageable pageable) {
        Page<Demand> demands = myPageDemandQueryRepository.findMyPageDemands(
                memberId, tripStatuses, category, period, pageable);

        List<UUID> tripIds = demands.getContent().stream()
                .map(Demand::getTripId)
                .distinct()
                .collect(Collectors.toList());
        Map<UUID, Trip> tripMap = tripRepository.findAllById(tripIds).stream()
                .collect(Collectors.toMap(Trip::getId, t -> t));

        return demands.map(d -> MyPageDemandResponse.of(d, tripMap.get(d.getTripId())));
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
