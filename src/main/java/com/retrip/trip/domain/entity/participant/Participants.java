package com.retrip.trip.domain.entity.participant;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.exception.NotLeaderException;
import com.retrip.trip.domain.exception.NotParticipantException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.vo.ParticipantRole;
import com.retrip.trip.domain.vo.ParticipantStatus;
import com.retrip.trip.domain.vo.TripStatus;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.NOT_TRIP_LEADER;
import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_MEMBER_NOT_IN_TRIP;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED, force = true)
public class Participants {

    public boolean updatableByLeader(UUID memberId) {
        return isLeader(memberId);
    }

    public boolean isLeader(UUID memberId) {
        return this.values.stream()
                .filter(m -> memberId.equals(m.getMemberId()))
                .findFirst()
                .orElseThrow(
                        () -> new InvalidValueException(ErrorCode.LEADER_REQUIRED, "여행 회원이 아닙니다."))
                .isLeader();
    }

    public void banMembers(UUID loginMemberId, List<UUID> memberIds, Trip trip) {
        validateTripRecruitingStatus(trip.getStatus());
        validateTripLeader(loginMemberId);
        validateExistParticipantMember(memberIds);

        List<Participant> participantsToBan =
                values.stream().filter(m -> memberIds.contains(m.getMemberId())).toList();

        participantsToBan.forEach(Participant::ban);
    }

    private void validateExistParticipantMember(List<UUID> memberIds) {
        boolean isAllExist = values.stream().anyMatch(m -> memberIds.contains(m.getMemberId()));

        if (!isAllExist) {
            throw new BusinessException(TRIP_MEMBER_NOT_IN_TRIP);
        }
    }

    private void validateTripLeader(UUID loginMemberId) {
        if (!isLeader(loginMemberId)) {
            throw new BusinessException(NOT_TRIP_LEADER);
        }
    }

    public void validateTripRecruitingStatus(TripStatus status) {
        if (!TripStatus.RECRUITING.equals(status)) {
            throw new IllegalStateException("해당 여행은 모집 중이 아닙니다.");
        }
    }

    public boolean isBan(UUID memberId) {
        return values.stream()
                .anyMatch(
                        tripParticipant ->
                                memberId.equals(tripParticipant.getMemberId())
                                        && tripParticipant.getStatus()
                                                == ParticipantStatus.EXPELLED);
    }

    public boolean anyDuplicate(List<UUID> memberIds) {
        return values.stream().anyMatch(p -> memberIds.contains(p.getMemberId()));
    }

    public void removeParticipant(UUID memberId) {
        this.values.removeIf(p -> p.getMemberId().equals(memberId));
    }

    public Optional<Participant> findParticipantById(UUID memberId) {
        return this.values.stream().filter(p -> p.getMemberId().equals(memberId)).findFirst();
    }

    public void delegateLeader(UUID currentLeaderId, UUID newLeaderId) {
        validateLeaderDelegation(currentLeaderId, newLeaderId);

        Participant oldLeader =
                findParticipantById(currentLeaderId)
                        .orElseThrow(() -> new NotParticipantException("현재 리더를 찾을 수 없습니다."));
        Participant newLeader =
                findParticipantById(newLeaderId)
                        .orElseThrow(
                                () ->
                                        new NotParticipantException(
                                                "새로운 리더가 될 멤버가 여행에 참여하고 있지 않습니다."));

        changeLeader(oldLeader, newLeader);
    }

    private void validateLeaderDelegation(UUID currentLeaderId, UUID newLeaderId) {
        if (currentLeaderId.equals(newLeaderId)) {
            throw new InvalidValueException("자기 자신에게 리더를 위임할 수 없습니다.");
        }
        if (!isLeader(currentLeaderId)) {
            throw new NotLeaderException();
        }
    }

    private void changeLeader(Participant oldLeader, Participant newLeader) {
        oldLeader.changeRole(ParticipantRole.PARTICIPANT);
        newLeader.changeRole(ParticipantRole.LEADER);
    }

    public boolean isFull() {
        // return this.values.size() >= this.maxParticipants;
        return this.values.size() >= 0;
    }
}
