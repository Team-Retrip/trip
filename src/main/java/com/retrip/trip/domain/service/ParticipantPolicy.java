package com.retrip.trip.domain.service;

import com.retrip.trip.domain.exception.NotLeaderException;
import com.retrip.trip.domain.exception.ParticipantFullException;
import com.retrip.trip.domain.vo.ParticipantRole;
import org.springframework.stereotype.Service;

@Service
public class ParticipantPolicy {

    public void validate(int maxParticipants, Long currentCount) {
        if (maxParticipants <= currentCount + 1) {
            throw new ParticipantFullException();
        }
    }

    public void validateLeader(ParticipantRole role) {
        if (!role.isLeader()) {
            throw new NotLeaderException();
        }
    }
}
