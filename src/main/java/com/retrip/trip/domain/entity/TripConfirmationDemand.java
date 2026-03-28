package com.retrip.trip.domain.entity;

import static com.retrip.trip.domain.exception.common.ErrorCode.NOT_FOUND_PARTICIPANTS;
import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_CONFIRMATION_PERIOD_OUT_OF_RANGE;
import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_CONFIRMATION_START_AFTER_END;
import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.exception.common.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED, force = true)
public class TripConfirmationDemand extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_confirmation_demand_to_trip")
    )
    private Trip trip;

    private LocalDate confirmStartDate;

    private LocalDate confirmEndDate;

    @Embedded
    private TripConfirmationReplies replies;

    private boolean expired;

    public static TripConfirmationDemand create(UUID loginMemberId, Trip trip, LocalDate start, LocalDate end) {
        validateConfirmationDemand(loginMemberId, trip, start, end);
        return TripConfirmationDemand.builder()
                .id(UUID.randomUUID())
                .trip(trip)
                .confirmStartDate(start)
                .confirmEndDate(end)
                .replies(new TripConfirmationReplies())
                .expired(false)
                .build();
    }

    public void addTripMember(UUID loginMemberId) {
        replies.addTripMember(this, this.trip, loginMemberId);
    }

    private static void validateConfirmationDemand(UUID loginMemberId, Trip trip, LocalDate start, LocalDate end) {
        trip.getTripParticipants().validateTripLeader(loginMemberId);
        trip.validateReadyTripStatus();

        if(trip.getTripParticipants().getValues().size() < 2){
            throw new BusinessException(NOT_FOUND_PARTICIPANTS);
        }
        if (start.isAfter(end)) {
            throw new BusinessException(TRIP_CONFIRMATION_START_AFTER_END);
        }
        if (start.isBefore(trip.getPeriod().getStart()) || end.isAfter(trip.getPeriod().getEnd())) {
            throw new BusinessException(TRIP_CONFIRMATION_PERIOD_OUT_OF_RANGE);
        }
    }

    public void demandAgain(UUID loginMemberId, LocalDate startDate, LocalDate endDate) {
        validateConfirmationDemand(loginMemberId, this.trip, startDate, endDate);

        this.confirmStartDate = startDate;
        this.confirmEndDate = endDate;
        this.replies.updatePendingStatus();
    }

    public boolean isAllAccepted() {
        return replies.getValues().stream().allMatch(TripConfirmationReply::isAccepted);
    }

    public void expire() {
        this.expired = true;
    }

    public void accept(UUID loginMemberId) {
        replies.accept(loginMemberId);
        if(isAllAccepted()) {
            this.trip.changeStatusToInProgress();
        }
    }

    public void reject(UUID loginMemberId) {
        replies.reject(loginMemberId);
    }
}
