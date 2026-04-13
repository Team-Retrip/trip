package com.retrip.trip.domain.service;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipants;
import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.exception.*;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.IllegalStateException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.retrip.trip.domain.vo.TripStatus.RECRUITING;

@Service
public class InvitationPolicy {
    public void canInvite(Trip trip, UUID leaderId, List<UUID> memberIds) {
        TripParticipants participants = trip.getTripParticipants();
        if (trip.getStatus().cannotCreateInvitations()) {
            throw new IllegalStateException("여행 초대를 생성할 수 없는 상태입니다. " + trip.getStatus().name());
        }

        if (isNotLeader(participants, leaderId)) {
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
        return !tripParticipants.requireLeader(leaderId);
    }

    public void canAccept(Trip trip, Invitation invitation) {
        if (invitation.isExpired()) {
            throw new InvitationExpiredException();
        }
        if (trip.getStatus() != RECRUITING) {
            throw new TripNotRecruitingException();
        }
        TripParticipants tripParticipants = trip.getTripParticipants();
        if (tripParticipants.isFull()) {
            throw new TripParticipantsIsFullException();
        }
    }

    public void canReject(Invitation invitation) {
        if (invitation.cannotReject()) {
            throw new InvitationRejectNotAllowedException();
        }
    }

    public void canDelete(Invitation invitation, UUID memberId) {
        if (!invitation.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
    }
}
