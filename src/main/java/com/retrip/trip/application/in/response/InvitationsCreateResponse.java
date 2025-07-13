package com.retrip.trip.application.in.response;

import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.vo.InvitationStatus;

import java.util.List;
import java.util.UUID;

public record InvitationsCreateResponse(
        UUID tripId,
        List<InvitationCreateResponse> invitations
) {
    public static InvitationsCreateResponse of(UUID tripId, List<Invitation> invitations) {
        return new InvitationsCreateResponse(
                tripId,
                invitations.stream()
                        .map(invitation -> new InvitationCreateResponse(
                                invitation.getMemberId(), invitation.getStatus())
                        ).toList()
        );
    }

    public record InvitationCreateResponse(
            UUID memberId,
            InvitationStatus status
    ) {
    }
}
