package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.TripInvitation;

import java.util.UUID;

public record TripInvitationsResponse(
        UUID tripId,
        UUID tripInvitationId,
        UUID memberId,
        String status
) {
    public static TripInvitationsResponse of(TripInvitation invitation) {
        return new TripInvitationsResponse(
                invitation.getTrip().getId(),
                invitation.getId(),
                invitation.getMemberId(),
                invitation.getStatus().name()
        );
    }
}
