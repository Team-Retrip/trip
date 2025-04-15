package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.TripDemand;
import com.retrip.trip.domain.entity.TripParticipant;
import java.util.UUID;

public record TripDemandResponse(
        UUID tripDemandId,
        UUID tripId,
        UUID memberId,
        String status
) {
    public static TripDemandResponse of(TripDemand tripDemand) {
        return new TripDemandResponse(
                tripDemand.getId(),
                tripDemand.getTrip().getId(),
                tripDemand.getMemberId(),
                tripDemand.getStatus().getViewName()
        );
    }
}
