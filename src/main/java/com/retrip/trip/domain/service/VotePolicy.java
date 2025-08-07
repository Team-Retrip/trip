package com.retrip.trip.domain.service;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipants;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VotePolicy {
    public void canCreate(Trip trip, UUID memberId) {
        validateParticipant(trip.getTripParticipants(), memberId);
    }

    private void validateParticipant(TripParticipants tripParticipants, UUID memberId) {
        /* TODO: Participants 애그리거트 분리되면 추가
        if (!tripParticipants.isMember(memberId)) {
            throw new NotParticipantException();
        }
         */
    }
}
