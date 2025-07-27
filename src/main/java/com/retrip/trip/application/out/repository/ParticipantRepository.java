package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.participant.Participant;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {}
