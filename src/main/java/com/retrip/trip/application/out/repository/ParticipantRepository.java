package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
}
