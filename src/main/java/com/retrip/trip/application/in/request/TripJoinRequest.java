package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TripJoinRequest(
        @NotNull UUID tripId,
        @NotNull UUID memberId
) {
    public TripParticipant to(Trip trip) {
        return TripParticipant.createTripParticipant(memberId, trip);
    }
}
