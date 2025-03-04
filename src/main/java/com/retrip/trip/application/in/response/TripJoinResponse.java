package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.TripParticipant;
import java.util.UUID;

public record TripJoinResponse(
        UUID tripId,
        UUID memberId,
        String status
) {
    public static TripJoinResponse of(TripParticipant participant) {
        return new TripJoinResponse(
                participant.getTrip().getId(),
                participant.getUserId(),
                participant.getStatus().getViewName()
        );
    }
}
