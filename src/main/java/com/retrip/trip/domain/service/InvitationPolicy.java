package com.retrip.trip.domain.service;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.participant.Participants;
import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.exception.*;
import com.retrip.trip.domain.exception.common.IllegalStateException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.retrip.trip.domain.vo.TripStatus.RECRUITING;

@Service
public class InvitationPolicy {
    public void canInvite(Trip trip, UUID leaderId, List<UUID> memberIds) {
        Participants participants = trip.getParticipants();
        if (trip.getStatus().cannotCreateInvitations()) {
            throw new IllegalStateException("여행 초대를 생성할 수 없는 상태입니다. " + trip.getStatus().name());
        }

        if (isNotLeader(trip.getParticipants(), leaderId)) {
            throw new NotLeaderException();
        }

        if (participants.anyDuplicate(memberIds)) {
            throw new TripInvitationDuplicateException("여행 멤버로 등록된 사용자는 초대할 수 없습니다.");
        }
    }

    public void canViewInvitations(Trip trip, UUID leaderId) {
        if (isNotLeader(trip.getParticipants(), leaderId)) {
            throw new NotLeaderException();
        }
    }

    public boolean isNotLeader(Participants participants, UUID leaderId) {
        return !participants.isLeader(leaderId);
    }

    public void canAccept(Trip trip, Invitation invitation) {
        if (invitation.isExpired()) {
            throw new InvitationExpiredException();
        }
        if (trip.getStatus() != RECRUITING) {
            throw new TripNotRecruitingException();
        }
        Participants participants = trip.getParticipants();
        if (participants.isFull()) {
            throw new TripParticipantsIsFullException();
        }
    }

    public void canReject(Invitation invitation) {
        if (invitation.cannotReject()) {
            throw new InvitationRejectNotAllowedException();
        }
    }
}
