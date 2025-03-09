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
public class TripParticipants {
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TripParticipant> values = new ArrayList<>();

    public TripParticipants(UUID memberId, Trip trip){
        TripParticipant leader = TripParticipant.createTripLeader(memberId, trip);
        values.add(leader);
    }

    public void addParticipant(TripParticipant participant) {
        values.add(participant);
    }

}

