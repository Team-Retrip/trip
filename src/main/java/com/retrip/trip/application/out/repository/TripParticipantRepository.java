package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, UUID> {
}
