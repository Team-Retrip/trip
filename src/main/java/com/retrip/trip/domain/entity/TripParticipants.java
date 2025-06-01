package com.retrip.trip.domain.entity;

import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.exception.TripFullException;
import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;

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



    public boolean isFullParticipants() {
        return values.size() >= maxParticipants;
    }

    public int getCurrentCount() {
        return values.size();
    }

    public boolean contains(UUID memberId) {
        return values.stream()
                .anyMatch(participant -> memberId.equals(participant.getMemberId()));
    }

    public void validateCanJoin() {

        if (isFullParticipants()) {
            throw new TripFullException();
        }
    }

    public boolean updatableByLeader(UUID memberId) {
        return isLeader(memberId);
    }

    public void updateMaxParticipants(int newMaxParticipants) {
        validateMaxParticipants(newMaxParticipants);
        validateNewMaxParticipants(newMaxParticipants);
        this.maxParticipants = newMaxParticipants;
    }

    private void validateMaxParticipants(int maxParticipants) {
        if (maxParticipants < 1) {
            throw new InvalidValueException(ErrorCode.INVALID_MAX_PARTICIPANTS, "최대 참여 인원은 1명 이상이어야 합니다.");
        }
    }

    private void validateNewMaxParticipants(int newMaxParticipants) {
        if (getCurrentCount() > newMaxParticipants) {
            throw new InvalidValueException(ErrorCode.INVALID_MAX_PARTICIPANTS, "현재 참여 인원보다 적은 수로 변경할 수 없습니다.");
        }
    }

    private boolean isLeader(UUID memberId) {
        return this.values.stream()
                .filter(m -> memberId.equals(m.getMemberId()))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.LEADER_REQUIRED, "여행 회원이 아닙니다."))
                .isLeader();
    }
}
