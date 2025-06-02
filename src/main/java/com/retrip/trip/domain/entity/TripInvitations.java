package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import com.retrip.trip.domain.exception.TripInvitationDuplicateException;
import com.retrip.trip.domain.exception.common.IllegalStateException;
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

    public void add(Trip trip, UUID leaderId, List<UUID> memberIds, TripParticipants participants) {
        validate(trip, leaderId, memberIds, participants);
        inviteAgain(memberIds);
        addNewInvitation(trip, memberIds);
    }

    private void validate(Trip trip, UUID leaderId, List<UUID> memberIds, TripParticipants participants) {
        if (trip.getStatus().cannotCreateInvitations()) {
            throw new IllegalStateException("여행 초대를 생성할 수 없는 상태입니다. " + trip.getStatus().name());
        }

        if (isNotLeader(trip.getTripParticipants(), leaderId)) {
            throw new MemberIsNotLeaderException();
        }

        if (participants.anyDuplicate(memberIds)) {
            throw new TripInvitationDuplicateException("여행 멤버로 등록된 사용자는 초대할 수 없습니다.");
        }
    }

    private boolean isNotLeader(TripParticipants tripParticipants, UUID leaderId) {
        return !tripParticipants.isLeader(leaderId);
    }

    private List<TripInvitation> findDuplicates(List<UUID> memberIds) {
        return values.stream()
                .filter(i -> memberIds.contains(i.getMemberId()))
                .toList();
    }

    private void addNewInvitation(Trip trip, List<UUID> memberIds) {
        List<UUID> invitationMemberIds = values.stream()
                .map(TripInvitation::getMemberId)
                .toList();

        memberIds.stream()
                .filter(id -> !invitationMemberIds.contains(id))
                .map(id -> new TripInvitation(trip, id))
                .forEach(values::add);
    }

    private void inviteAgain(List<UUID> memberIds) {
        if (anyCannotInviteAgain(memberIds)) {
            throw new TripInvitationDuplicateException();
        }
        findDuplicates(memberIds)
                .forEach(TripInvitation::inviteAgain);
    }

    private boolean anyCannotInviteAgain(List<UUID> memberIds) {
        return findDuplicates(memberIds).stream()
                .anyMatch(TripInvitation::cannotInviteAgain);
    }
}
