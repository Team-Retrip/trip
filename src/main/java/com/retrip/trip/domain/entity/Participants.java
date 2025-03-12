package com.retrip.trip.domain.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.CascadeType;
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
public class Participants {
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TripParticipant> values = new ArrayList<>();

    public Participants(UUID memberId, Trip trip) {
        TripParticipant leader = TripParticipant.createTripLeader(memberId, trip);
        values.add(leader);
    }


    public void updateByLeader(UUID updateId) {
        TripParticipant updateUser = getUpdateUser(updateId);
        if (!updateUser.isLeader()) {
            throw new IllegalArgumentException("리더만 일정을 변경할 수 있습니다.");
        }
    }

    private TripParticipant getUpdateUser(UUID updateId) {
        List<TripParticipant> tripParticipants = values.stream().filter(tripParticipant -> tripParticipant.getUserId().equals(updateId)).toList();
        if (tripParticipants.size() != 1) {
            throw new IllegalArgumentException("해당 참여자가 1명 미만 또는 1명 초과 입니다.");
        }
        return tripParticipants.getFirst();
    }
}

