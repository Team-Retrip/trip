package com.retrip.trip.application.in.usecase;

import com.retrip.trip.domain.entity.participant.Participant;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ParticipantManageUseCase {
    Participant createLeaderParticipant(UUID tripId, UUID memberId);

    List<Participant> findByMemberId(UUID memberId, Pageable page);

    Long findByMemberIdTotalCount(UUID memberId);

    Participant createParticipant(UUID tripId, UUID memberId, int maxParticipants);

    void requireLeaderOrElseThrow(UUID tripId, UUID uuid);

    void requireParticipantOrElseThrow(UUID tripId, UUID uuid);

    void remove(UUID tripId, UUID memberId);

    Participant delegateLeader(UUID tripId, UUID currentLeaderId, UUID newLeaderId);

    void canInvite(UUID tripId, UUID leaderId, List<UUID> memberIds);

    List<Participant> banMembers(UUID tripId, UUID loginMemberId, List<UUID> memberIds);

    void canDemand(UUID tripId, UUID memberId);
}
