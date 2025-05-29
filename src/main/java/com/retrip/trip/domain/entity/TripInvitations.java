package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.TripInvitationDuplicateException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
@Embeddable
public class TripInvitations {
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TripInvitation> values = new ArrayList<>();

    public void add(Trip trip, UUID leaderId, List<UUID> memberIds) {
        validate(trip, leaderId, memberIds);
        memberIds.stream()
                .map(id -> new TripInvitation(trip, id))
                .forEach(values::add);
    }

    private void validate(Trip trip, UUID leaderId, List<UUID> memberIds) {
        if (isNotLeader(trip.getTripParticipants(), leaderId)) {
            throw new MemberIsNotLeaderException();
        }

        if (anyDuplicate(memberIds)) {
            throw new TripInvitationDuplicateException();
        }
    }

    private boolean isNotLeader(TripParticipants tripParticipants, UUID leaderId) {
        return !tripParticipants.isLeader(leaderId);
    }

    private boolean anyDuplicate(List<UUID> memberIds) {
        return values.stream()
                .map(TripInvitation::getMemberId)
                .anyMatch(memberIds::contains);
    }
}
