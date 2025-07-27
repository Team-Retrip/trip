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

    void updateByLeaderOrThrow(UUID tripId, UUID uuid);
}
