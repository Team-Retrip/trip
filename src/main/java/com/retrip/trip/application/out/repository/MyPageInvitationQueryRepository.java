package com.retrip.trip.application.out.repository;

import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MyPageInvitationQueryRepository {
    Page<Invitation> findMyPageInvitations(
            UUID memberId,
            List<TripStatus> tripStatuses,
            TripCategory category,
            String period,
            Pageable pageable
    );
}
