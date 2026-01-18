package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.NotParticipantException;
import com.retrip.trip.domain.exception.TripFullException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.vo.ParticipantRole;
import com.retrip.trip.domain.vo.ParticipantStatus;
import com.retrip.trip.domain.vo.TripStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.*;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED, force = true)
public class TripParticipants {
    @Column(name = "max_participants", nullable = false)
    private int maxParticipants;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TripParticipant> values = new ArrayList<>();

    public TripParticipants(UUID memberId, Trip trip, int maxParticipants) {
        validateMaxParticipants(maxParticipants);
        this.maxParticipants = maxParticipants;
        TripParticipant leader = TripParticipant.createTripLeader(memberId, trip);
        values.add(leader);
    }

    public void addParticipant(TripParticipant participant) {
        validateCanJoin();
        values.add(participant);
    }

    public int getCurrentCount() {
        return values.size();
    }

    public void validateCanJoin() {
        if (isFull()) {
            throw new TripFullException();
        }
    }

    public boolean updatableByLeader(UUID memberId) {
        return requireLeader(memberId);
    }

    public void updateMaxParticipants(int newMaxParticipants, UUID memberId) {
        if (!requireLeader(memberId)) {
            throw new MemberIsNotLeaderException();
        }
        validateMaxParticipants(newMaxParticipants);
        validateNewMaxParticipants(newMaxParticipants);
        this.maxParticipants = newMaxParticipants;
    }

    private void validateMaxParticipants(int maxParticipants) {
        if (maxParticipants < 1) {
            throw new InvalidValueException(INVALID_MAX_PARTICIPANTS_VALUE);
        }
    }

    private void validateNewMaxParticipants(int newMaxParticipants) {
        if (getCurrentCount() > newMaxParticipants) {
            throw new InvalidValueException(MAX_PARTICIPANTS_LESS_THAN_CURRENT);
        }
    }

    public boolean requireLeader(UUID memberId) {
        return this.values.stream()
                .filter(m -> memberId.equals(m.getMemberId()))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.LEADER_REQUIRED, "여행 회원이 아닙니다."))
                .isLeader();
    }

    public boolean isLeader(UUID memberId) {
        return this.values.stream()
                .anyMatch(m -> memberId.equals(m.getMemberId()) && m.isLeader());
    }

    public boolean isParticipant(UUID memberId) {
        return this.values.stream()
                .anyMatch(m -> memberId.equals(m.getMemberId()));
    }

    public void banMembers(UUID loginMemberId, List<UUID> memberIds, Trip trip) {
        validateTripRecruitingStatus(trip.getStatus());
        validateTripLeader(loginMemberId);
        validateExistParticipantMember(memberIds);

        List<TripParticipant> participantsToBan = values.stream()
                .filter(m -> memberIds.contains(m.getMemberId()))
                .toList();

        participantsToBan.forEach(TripParticipant::ban);
    }

    private void validateExistParticipantMember(List<UUID> memberIds) {
        boolean isAllExist = values.stream()
                .anyMatch(m -> memberIds.contains(m.getMemberId()));

        if(!isAllExist) {
            throw new BusinessException(TRIP_MEMBER_NOT_IN_TRIP);
        }
    }

    public void validateTripLeader(UUID loginMemberId) {
        if(!requireLeader(loginMemberId)) {
            throw new BusinessException(NOT_TRIP_LEADER);
        }
    }

    public void validateTripRecruitingStatus(TripStatus status) {
        if (!TripStatus.RECRUITING.equals(status)) {
            throw new BusinessException(TRIP_NOT_RECRUITING);
        }
    }

    public boolean isBan(UUID memberId) {
        return values.stream()
                .anyMatch(tripParticipant -> memberId.equals(tripParticipant.getMemberId()) &&
                        tripParticipant.getStatus() == ParticipantStatus.EXPELLED);
    }

    public boolean anyDuplicate(List<UUID> memberIds) {
        return values.stream()
                .anyMatch(p -> memberIds.contains(p.getMemberId()));
    }

    public void removeParticipant(UUID memberId) {
        this.values.removeIf(p -> p.getMemberId().equals(memberId));
    }

    public Optional<TripParticipant> findParticipantById(UUID memberId) {
        return this.values.stream()
                .filter(p -> p.getMemberId().equals(memberId))
                .findFirst();
    }

    public void delegateLeader(UUID currentLeaderId, UUID newLeaderId) {
        validateLeaderDelegation(currentLeaderId, newLeaderId);

        TripParticipant oldLeader = findParticipantById(currentLeaderId)
                .orElseThrow(() -> new NotParticipantException("현재 리더를 찾을 수 없습니다."));
        TripParticipant newLeader = findParticipantById(newLeaderId)
                .orElseThrow(() -> new NotParticipantException("새로운 리더가 될 멤버가 여행에 참여하고 있지 않습니다."));

        changeLeader(oldLeader, newLeader);
    }

    private void validateLeaderDelegation(UUID currentLeaderId, UUID newLeaderId) {
        if (currentLeaderId.equals(newLeaderId)) {
            throw new BusinessException(CANNOT_DELEGATE_LEADER_TO_SELF);
        }
        if (!requireLeader(currentLeaderId)) {
            throw new MemberIsNotLeaderException();
        }
    }

    private void changeLeader(TripParticipant oldLeader, TripParticipant newLeader) {
        oldLeader.changeRole(ParticipantRole.PARTICIPANT);
        newLeader.changeRole(ParticipantRole.LEADER);
    }

    public boolean isFull() {
        return this.values.size() >= this.maxParticipants;
    }
}

