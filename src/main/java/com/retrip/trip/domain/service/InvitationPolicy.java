package com.retrip.trip.domain.service;

import com.retrip.trip.domain.entity.Trip;
import com.retrip.trip.domain.entity.invitation.Invitation;
import com.retrip.trip.domain.exception.*;
import com.retrip.trip.domain.exception.common.IllegalStateException;
import org.springframework.stereotype.Service;

import static com.retrip.trip.domain.vo.TripStatus.RECRUITING;

@Service
public class InvitationPolicy {
    public void canInvite(Trip trip) {
        if (trip.getStatus().cannotCreateInvitations()) {
            throw new IllegalStateException("여행 초대를 생성할 수 없는 상태입니다. " + trip.getStatus().name());
        }
    }

    public void canAccept(Trip trip, Invitation invitation) {
        if (invitation.isExpired()) {
            throw new InvitationExpiredException();
        }
        if (trip.getStatus() != RECRUITING) {
            throw new TripNotRecruitingException();
        }
    }

    public void canReject(Invitation invitation) {
        if (invitation.cannotReject()) {
            throw new InvitationRejectNotAllowedException();
        }
    }
}
