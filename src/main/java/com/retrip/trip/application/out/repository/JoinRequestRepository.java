package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.JoinRequest;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, UUID> {
}