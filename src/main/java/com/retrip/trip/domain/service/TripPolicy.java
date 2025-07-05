package com.retrip.trip.domain.service;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.exception.MemberIsNotLeaderException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TripPolicy {
    public void validateLeader(Trip trip, UUID leaderId) {
        if (!trip.getTripParticipants().isLeader(leaderId)) {
            throw new MemberIsNotLeaderException();
        }
    }
}
