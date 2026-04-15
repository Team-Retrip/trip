package com.retrip.trip.domain.service;

import static com.retrip.trip.domain.exception.common.ErrorCode.DEMAND_CANCEL_NOT_ALLOWED;
import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_DEMAND_NOT_ALLOWED;
import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_DEMAND_STATUS_NOT_PENDING;
import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_MEMBER_BANNED_CANNOT_APPLY;
import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_NOT_RECRUITING;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipants;
import com.retrip.trip.domain.entity.demand.Demand;
import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.TripParticipantsIsFullException;
import com.retrip.trip.domain.exception.common.BusinessException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DemandPolicy {

    public void canDemand(UUID memberId, Trip trip, List<Demand> savedDemands) {
        TripParticipants participants = trip.getTripParticipants();

        if (participants.isBan(memberId)) {
            throw new BusinessException(TRIP_MEMBER_BANNED_CANNOT_APPLY);
        }

        if (isAlreadyDemanded(memberId, trip.getId(), savedDemands)) {
            throw new BusinessException(TRIP_DEMAND_NOT_ALLOWED);
        }

        if(trip.isNotTripRecruitingStatus()){
            throw new BusinessException(TRIP_NOT_RECRUITING);
        }

        if (participants.isFull()) {
            throw new TripParticipantsIsFullException();
        }
    }

    private boolean isAlreadyDemanded(UUID memberId, UUID tripId, List<Demand> savedDemands) {
        return savedDemands.stream()
                .anyMatch(demand -> demand.isAlreadyDemanded(memberId, tripId));
    }

    public void canApprove(UUID leaderId, Trip trip, Demand demand) {
        TripParticipants participants = trip.getTripParticipants();

        if (isNotLeader(participants, leaderId)) {
            throw new MemberIsNotLeaderException();
        }

        if(demand.isNotPendingStatus()){
            throw new BusinessException(TRIP_DEMAND_STATUS_NOT_PENDING);
        }
    }

    public void canReject(UUID leaderId, Trip trip, Demand demand) {
        TripParticipants participants = trip.getTripParticipants();

        if (isNotLeader(participants, leaderId)) {
            throw new MemberIsNotLeaderException();
        }

        if(demand.isNotPendingStatus()){
            throw new BusinessException(TRIP_DEMAND_STATUS_NOT_PENDING);
        }
    }

    public void canViewDemands(UUID leaderId, Trip trip) {
        TripParticipants participants = trip.getTripParticipants();

        if (isNotLeader(participants, leaderId)) {
            throw new MemberIsNotLeaderException();
        }
    }

    public void canCancel(UUID memberId, Demand demand) {
        if (demand.isNotOwner(memberId)) {
            throw new BusinessException(DEMAND_CANCEL_NOT_ALLOWED);
        }

        if (demand.isNotPendingStatus()) {
            throw new BusinessException(DEMAND_CANCEL_NOT_ALLOWED);
        }
    }

    public boolean isNotLeader(TripParticipants tripParticipants, UUID leaderId) {
        return !tripParticipants.requireLeader(leaderId);
    }
}
