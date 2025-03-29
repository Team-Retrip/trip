package com.retrip.trip.application.in.request;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripDemand;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TripDemandRequest(
        @NotNull UUID memberId,
        String message
) {
    public TripDemand to(Trip trip) {
        return TripDemand.create(memberId, trip, message);
    }
}
