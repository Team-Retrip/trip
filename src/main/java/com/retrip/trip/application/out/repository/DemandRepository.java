package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.demand.Demand;
import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.vo.InvitationStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandRepository extends JpaRepository<Demand, UUID> {
    List<Demand> findAllByTripId(UUID tripId);
}
