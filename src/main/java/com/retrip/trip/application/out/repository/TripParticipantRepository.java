package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.TripParticipant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface TripParticipantRepository extends ReadRepository<TripParticipant, UUID> {
}
