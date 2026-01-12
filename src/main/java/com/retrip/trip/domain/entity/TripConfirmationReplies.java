package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.common.BusinessException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.retrip.trip.domain.exception.common.ErrorCode.TARGET_ENTITY_NOT_FOUND;

@Getter
@Embeddable
@NoArgsConstructor
public class TripConfirmationReplies {

    @OneToMany(mappedBy = "confirmationDemand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripConfirmationReply> values = new ArrayList<>();

    public void addTripMember(TripConfirmationDemand tripConfirmationDemand, Trip trip, UUID loginMemberId) {
        trip.getTripParticipants().getValues().stream()
                .filter(participant -> !participant.getMemberId().equals(loginMemberId))
                .forEach(participant -> values.add(TripConfirmationReply.create(tripConfirmationDemand, participant.getMemberId())));
    }

    public void updatePendingStatus() {
        values.forEach(TripConfirmationReply::pending);
    }

    public void accept(UUID loginMemberId) {
        TripConfirmationReply reply = values.stream()
                .filter(tripConfirmationReply -> tripConfirmationReply.getMemberId().equals(loginMemberId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(TARGET_ENTITY_NOT_FOUND));

        reply.accept();
    }

    public void reject(UUID loginMemberId) {
        TripConfirmationReply reply = values.stream()
                .filter(tripConfirmationReply -> tripConfirmationReply.getMemberId().equals(loginMemberId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(TARGET_ENTITY_NOT_FOUND));

        reply.reject();
    }
}

