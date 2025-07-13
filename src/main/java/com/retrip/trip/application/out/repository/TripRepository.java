package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    @Query("select t FROM Trip t JOIN FETCH t.tripParticipants WHERE t.id = :id")
    Optional<Trip> findWithParticipantsById(UUID id);
}
