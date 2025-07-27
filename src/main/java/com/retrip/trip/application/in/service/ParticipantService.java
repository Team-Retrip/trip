package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.usecase.ParticipantManageUseCase;

import com.retrip.trip.application.out.repository.ParticipantQueryRepository;
import com.retrip.trip.application.out.repository.ParticipantRepository;
import com.retrip.trip.domain.entity.participant.Participant;

import com.retrip.trip.domain.exception.common.EntityNotFoundException;
import com.retrip.trip.domain.service.ParticipantPolicy;

import com.retrip.trip.domain.vo.ParticipantRole;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantService implements ParticipantManageUseCase {
    private final ParticipantPolicy participantPolicy;
    private final ParticipantRepository participantRepository;
    private final ParticipantQueryRepository participantQueryRepository;

    @Override
    public Participant createLeaderParticipant(UUID tripId, UUID memberId) {
        return participantRepository.save(
                Participant.create(tripId, memberId, ParticipantRole.LEADER));
    }

    @Override
    public List<Participant> findByMemberId(UUID memberId, Pageable page) {
        return participantQueryRepository.findByMemberId(memberId, page);
    }

    @Override
    public Long findByMemberIdTotalCount(UUID memberId) {
        return participantQueryRepository.findByMemberId(memberId);
    }

    @Override
    public Participant createParticipant(UUID tripId, UUID memberId, int maxParticipants) {
        Long currentCount = participantQueryRepository.findByTripIdCount(memberId);
        participantPolicy.validate(maxParticipants, currentCount);
        return participantRepository.save(
                Participant.create(tripId, memberId, ParticipantRole.PARTICIPANT));
    }

    @Override
    public void updateByLeaderOrThrow(UUID tripId, UUID memberId) {
        Participant participant =
                participantQueryRepository
                        .findByTripIdAndMemberId(tripId, memberId)
                        .orElseThrow(EntityNotFoundException::new);
        participantPolicy.validateLeader(participant.getRole());
    }
}
