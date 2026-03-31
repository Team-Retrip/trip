package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.vo.InvitationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    List<Invitation> findByTripId(UUID tripId);

    Page<Invitation> findByTripId(UUID tripId, Pageable page);

    Page<Invitation> findByTripIdAndStatus(UUID tripId, InvitationStatus status, Pageable page);

    Page<Invitation> findByMemberId(UUID memberId, Pageable pageable);

    Page<Invitation> findByMemberIdAndStatus(UUID memberId, InvitationStatus status, Pageable pageable);

    Optional<Invitation> findByTripIdAndMemberIdAndStatus(UUID tripId, UUID memberId, InvitationStatus status);
}
