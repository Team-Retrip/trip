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
public class TripDestinations {

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TripDestination> values = new ArrayList<>();

    public TripDestinations(Trip trip, List<UUID> destinationIds) {
        values.addAll(destinationIds.stream()
                .map(id -> TripDestination.of(trip, id))
                .toList());
    }

    public List<UUID> getDestinationIds() {
        return values.stream()
                .map(TripDestination::getDestinationId)
                .toList();
    }
}
