package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    @Query("select t " +
            "from Trip t " +
            "left join fetch t.invitations.values ti " +
            "where t.id = :tripId")
    Optional<Trip> findWithTripInvitations(UUID tripId);
}
