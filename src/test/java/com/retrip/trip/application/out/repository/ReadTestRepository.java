package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.Trip;

import java.util.List;
import java.util.UUID;

public interface ReadTestRepository extends ReadRepository<Trip, UUID> {
    List<Trip> findByOpenTrue();
}
