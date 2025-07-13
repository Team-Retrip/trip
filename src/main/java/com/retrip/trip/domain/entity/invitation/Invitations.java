package com.retrip.trip.domain.entity.invitation;

import com.retrip.trip.domain.exception.TripInvitationDuplicateException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
@AllArgsConstructor
public class Invitations {
    private List<Invitation> values = new ArrayList<>();

    public void add(UUID tripId, List<UUID> memberIds) {
        inviteAgain(memberIds);
        addNewInvitation(tripId, memberIds);
    }

    private List<Invitation> findDuplicates(List<UUID> memberIds) {
        return values.stream()
                .filter(i -> memberIds.contains(i.getMemberId()))
                .toList();
    }

    private void addNewInvitation(UUID tripId, List<UUID> memberIds) {
        List<UUID> invitationMemberIds = values.stream()
                .map(Invitation::getMemberId)
                .toList();

        memberIds.stream()
                .filter(id -> !invitationMemberIds.contains(id))
                .map(id -> new Invitation(tripId, id))
                .forEach(values::add);
    }

    private void inviteAgain(List<UUID> memberIds) {
        if (anyCannotInviteAgain(memberIds)) {
            throw new TripInvitationDuplicateException();
        }
        findDuplicates(memberIds)
                .forEach(Invitation::inviteAgain);
    }

    private boolean anyCannotInviteAgain(List<UUID> memberIds) {
        return findDuplicates(memberIds).stream()
                .anyMatch(Invitation::cannotInviteAgain);
    }
}
