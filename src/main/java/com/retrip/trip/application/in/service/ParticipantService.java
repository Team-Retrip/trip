package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.usecase.ParticipantManageUseCase;

import com.retrip.trip.application.out.repository.ParticipantQueryRepository;
import com.retrip.trip.application.out.repository.ParticipantRepository;
import com.retrip.trip.domain.entity.participant.Participant;

import com.retrip.trip.domain.exception.NotParticipantException;
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
@Transactional
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
    @Transactional(readOnly = true)
    public List<Participant> findByMemberId(UUID memberId, Pageable page) {
        return participantQueryRepository.findByMemberId(memberId, page);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public void requireLeaderOrElseThrow(UUID tripId, UUID memberId) {
        Participant participant =
                participantQueryRepository
                        .findByTripIdAndMemberId(tripId, memberId)
                        .orElseThrow(EntityNotFoundException::new);
        participantPolicy.validateLeader(participant.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public void requireParticipantOrElseThrow(UUID tripId, UUID memberId) {
        Participant participant =
                participantQueryRepository
                        .findByTripIdAndMemberId(tripId, memberId)
                        .orElseThrow(EntityNotFoundException::new);
        participantPolicy.validateParticipant(participant.getRole());
    }

    @Override
    public void remove(UUID tripId, UUID memberId) {
        // todo: SoftDelete 변경 필요
        Participant participant =
                participantQueryRepository
                        .findByTripIdAndMemberId(tripId, memberId)
                        .orElseThrow(EntityNotFoundException::new);
        participantRepository.delete(participant);
    }

    @Override
    public Participant delegateLeader(UUID tripId, UUID currentLeaderId, UUID newLeaderId) {

        Participant oldLeader =
                participantQueryRepository
                        .findByTripIdAndMemberId(tripId, currentLeaderId)
                        .orElseThrow(() -> new NotParticipantException("현재 리더를 찾을 수 없습니다."));
        Participant newLeader =
                participantQueryRepository
                        .findByTripIdAndMemberId(tripId, newLeaderId)
                        .orElseThrow(
                                () ->
                                        new NotParticipantException(
                                                "새로운 리더가 될 멤버가 여행에 참여하고 있지 않습니다."));
        participantPolicy.validateDelegate(oldLeader, newLeader);
        participantPolicy.changeLeader(oldLeader, newLeader);
        return newLeader;
    }

    @Override
    public void canInvite(UUID tripId, UUID leaderId, List<UUID> memberIds) {
        requireLeaderOrElseThrow(tripId, leaderId);
        List<Participant> participants = participantRepository.findByTripId(tripId);
        participantPolicy.validateInvite(participants, memberIds);
    }

    @Override
    public List<Participant> banMembers(UUID tripId, UUID loginMemberId, List<UUID> memberIds) {
        requireLeaderOrElseThrow(tripId, loginMemberId);
        List<Participant> participants = participantRepository.findByTripId(tripId);
        participantPolicy.validateBan(participants, memberIds);
        List<Participant> participantsToBan =
                participants.stream().filter(m -> memberIds.contains(m.getMemberId())).toList();
        participantsToBan.forEach(Participant::ban);
        return participantsToBan;
    }

    @Override
    public void canDemand(UUID tripId, UUID memberId) {
        Participant participant =
                participantQueryRepository
                        .findByTripIdAndMemberId(tripId, memberId)
                        .orElseThrow(EntityNotFoundException::new);
        participantPolicy.validateDemand(participant);
    }
}
