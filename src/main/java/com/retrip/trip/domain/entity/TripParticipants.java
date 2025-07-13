package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.NotParticipantException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.vo.ParticipantRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED, force = true)
public class TripParticipants {
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TripParticipant> values = new ArrayList<>();

    public TripParticipants(UUID leaderId, Trip trip) {
        TripParticipant leader = TripParticipant.createTripLeader(leaderId, trip);
        values.add(leader);
    }

    public void addParticipant(TripParticipant participant) {
        values.add(participant);
    }

    public boolean updatableByLeader(UUID memberId) {
        return isLeader(memberId);
    }

    public boolean isLeader(UUID memberId) {
        return this.values.stream()
                .filter(m -> memberId.equals(m.getMemberId()))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("여행 회원이 아닙니다."))
                .isLeader();
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
            throw new InvalidValueException("자기 자신에게 리더를 위임할 수 없습니다.");
        }
        if (!isLeader(currentLeaderId)) {
            throw new MemberIsNotLeaderException();
        }
    }

    private void changeLeader(TripParticipant oldLeader, TripParticipant newLeader) {
        oldLeader.changeRole(ParticipantRole.PARTICIPANT);
        newLeader.changeRole(ParticipantRole.LEADER);
    }
}