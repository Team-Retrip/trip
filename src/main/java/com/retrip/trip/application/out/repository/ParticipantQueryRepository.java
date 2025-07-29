package com.retrip.trip.application.out.repository;

import com.retrip.trip.application.in.response.TripResponse;
import com.retrip.trip.domain.entity.Trip;

import com.retrip.trip.domain.entity.participant.Participant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantQueryRepository {
    Long findByMemberId(UUID memberId);

    List<Participant> findByMemberId(UUID memberId, Pageable page);

    Long findByTripIdCount(UUID tripId);

    Optional<Participant> findByTripIdAndMemberId(UUID tripId, UUID memberId);

    Optional<Participant> findByTripIdAndMemberIdAndAllStatus(UUID tripId, UUID memberId);
}
