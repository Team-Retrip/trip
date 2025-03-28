package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.JoinRequest;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TripJoinApplyRequest(
        @NotNull UUID tripId,
        @NotNull UUID memberId,
        String message
) {
    public JoinRequest to(Trip trip) {
        return JoinRequest.create(memberId, trip, message);
    }
}
