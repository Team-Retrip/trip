package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.common.InvalidValueException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
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
}

