package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
}
