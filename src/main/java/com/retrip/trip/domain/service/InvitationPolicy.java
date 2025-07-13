package com.retrip.trip.domain.service;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipants;
import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.TripInvitationDuplicateException;
import com.retrip.trip.domain.exception.common.IllegalStateException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InvitationPolicy {
    public void canInvite(Trip trip, UUID leaderId, List<UUID> memberIds) {
        TripParticipants participants = trip.getTripParticipants();
        if (trip.getStatus().cannotCreateInvitations()) {
            throw new IllegalStateException("여행 초대를 생성할 수 없는 상태입니다. " + trip.getStatus().name());
        }

        if (isNotLeader(trip.getTripParticipants(), leaderId)) {
            throw new MemberIsNotLeaderException();
        }

        if (participants.anyDuplicate(memberIds)) {
            throw new TripInvitationDuplicateException("여행 멤버로 등록된 사용자는 초대할 수 없습니다.");
        }
    }

    public void canViewInvitations(Trip trip, UUID leaderId) {
        if (isNotLeader(trip.getTripParticipants(), leaderId)) {
            throw new MemberIsNotLeaderException();
        }
    }

    public boolean isNotLeader(TripParticipants tripParticipants, UUID leaderId) {
        return !tripParticipants.isLeader(leaderId);
    }
}
