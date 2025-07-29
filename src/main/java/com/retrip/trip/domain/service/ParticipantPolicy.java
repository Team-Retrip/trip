package com.retrip.trip.domain.service;

import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_MEMBER_BANNED_CANNOT_APPLY;
import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_MEMBER_NOT_IN_TRIP;

import com.retrip.trip.domain.entity.participant.Participant;
import com.retrip.trip.domain.exception.NotLeaderException;
import com.retrip.trip.domain.exception.NotParticipantException;
import com.retrip.trip.domain.exception.ParticipantFullException;
import com.retrip.trip.domain.exception.TripInvitationDuplicateException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.vo.ParticipantRole;

import java.util.List;
import java.util.Optional;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ParticipantPolicy {

    public void validate(int maxParticipants, Long currentCount) {
        if (maxParticipants <= currentCount) {
            throw new ParticipantFullException();
        }
    }

    public void validateLeader(ParticipantRole role) {
        if (!role.isLeader()) {
            throw new NotLeaderException();
        }
    }

    public void validateParticipant(ParticipantRole role) {
        if (!role.isParticipant()) {
            throw new NotParticipantException("회원은 참여자가 아닙니다.");
        }
    }

    public void validateDelegate(Participant currentLeader, Participant newLeader) {
        if (currentLeader.equals(newLeader)) {
            throw new InvalidValueException("자기 자신에게 리더를 위임할 수 없습니다.");
        }
        if (!currentLeader.getRole().isLeader()) {
            throw new NotLeaderException();
        }
    }

    public void changeLeader(Participant oldLeader, Participant newLeader) {
        oldLeader.changeRole(ParticipantRole.PARTICIPANT);
        newLeader.changeRole(ParticipantRole.LEADER);
    }

    public void validateInvite(List<Participant> participants, List<UUID> memberIds) {
        if (participants.stream().anyMatch(p -> memberIds.contains(p.getMemberId()))) {
            throw new TripInvitationDuplicateException("여행 멤버로 등록된 사용자는 초대할 수 없습니다.");
        }
    }

    public void validateBan(List<Participant> participants, List<UUID> memberIds) {
        if (participants.stream().noneMatch(p -> memberIds.contains(p.getMemberId()))) {
            throw new BusinessException(TRIP_MEMBER_NOT_IN_TRIP);
        }
    }

    public void validateDemand(Optional<Participant> participant, Long count, int maxParticipants) {
        if (count.intValue() + 1 >= maxParticipants) {
            throw new ParticipantFullException();
        }

        if (participant.isPresent() && participant.get().getStatus().isExpelled()) {
            throw new BusinessException(TRIP_MEMBER_BANNED_CANNOT_APPLY);
        }
    }
}
