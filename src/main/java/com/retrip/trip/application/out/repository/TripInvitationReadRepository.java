package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.TripInvitation;
import com.retrip.trip.domain.vo.TripInvitationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TripInvitationReadRepository extends ReadRepository<TripInvitation, UUID> {
    Page<TripInvitation> findByTripIdAndStatus(UUID tripId, TripInvitationStatus status, Pageable page);

    Page<TripInvitation> findByMemberIdAndStatus(UUID memberId, TripInvitationStatus status, Pageable pageable);
}
