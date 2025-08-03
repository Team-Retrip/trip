package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.LeaderCannotLeaveException;
import com.retrip.trip.domain.exception.NotParticipantException;
import com.retrip.trip.domain.exception.PeriodUpdateFailedException;
import com.retrip.trip.domain.exception.TripNotReadyException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.vo.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.NOT_TRIP_READY_STATUS;
import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_MEMBER_BANNED_CANNOT_APPLY;
import static com.retrip.trip.domain.vo.TripStatus.BEFORE_TRIP;
import static com.retrip.trip.domain.vo.TripStatus.RECRUITMENT_CLOSED;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Trip extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;
    private UUID destinationId;

    @Version
    private long version;

    @Embedded
    private TripTitle title;

    @Embedded
    private TripDescription description;

    private boolean open;

    @Column(name = "status", length = 50, nullable = false)
    private TripStatus status;

    @Column(name = "category", length = 50, nullable = false)
    private TripCategory category;

    @Embedded
    private TripParticipants tripParticipants;

    @Embedded
    private TripDemands tripDemands;

    @Embedded
    private TripConfirmationDemands tripConfirmationDemands;

    @Embedded
    private TripPeriod period;

    @Embedded
    private Itineraries itineraries;

    public static Trip create(
            UUID memberId,
            UUID destinationId,
            TripTitle title,
            TripDescription description,
            TripPeriod period,
            boolean open,
            int maxParticipants,
            TripCategory category
    ) {
        Trip trip = Trip.builder()
                .id(UUID.randomUUID())
                .destinationId(destinationId)
                .title(title)
                .description(description)
                .period(period)
                .open(open)
                .category(category)
                .status(TripStatus.RECRUITING)
                .tripDemands(new TripDemands())
                .build();
        trip.tripParticipants = new TripParticipants(memberId, trip, maxParticipants);
        return trip;
    }

    public static Trip createWithItineraries(
            UUID leaderId,
            UUID destinationId,
            TripTitle title,
            TripDescription description,
            TripPeriod period,
            boolean open,
            int maxParticipants,
            TripCategory category
    ) {
        Trip trip = Trip.builder()
                .id(UUID.randomUUID())
                .destinationId(destinationId)
                .title(title)
                .description(description)
                .period(period)
                .open(open)
                .category(category)
                .status(TripStatus.RECRUITING)
                .build();
        trip.itineraries = new Itineraries(trip, period);
        trip.tripParticipants = new TripParticipants(leaderId, trip, maxParticipants);
        return trip;
    }

    public void addParticipant(TripParticipant participant) {
        this.tripParticipants.addParticipant(participant);
    }

    public void addDemand(TripDemand demand) {
        validateAddDemand(demand);
        validateParticipantLimitNotExceeded();
        this.tripDemands.addDemand(demand);
    }

    private void validateAddDemand(TripDemand demand) {
        if (this.tripParticipants.isBan(demand.getMemberId())) {
            throw new BusinessException(TRIP_MEMBER_BANNED_CANNOT_APPLY);
        }
    }

    public void validateParticipantLimitNotExceeded() {
        tripParticipants.validateCanJoin();
    }

    public void updatePeriod(
            TripPeriod period,
            @NotNull UUID memberId) {
        if (!tripParticipants.updatableByLeader(memberId)) {
            throw new PeriodUpdateFailedException();
        }
        this.period = period;
        if (Objects.isNull(this.itineraries)) {
            this.itineraries = new Itineraries(this, period);
        } else {
            this.itineraries.updateByPeriod(period, this);
        }
    }

    public List<UUID> getItinerariesIds() {
        if (Objects.isNull(getItineraries())) {
            return List.of();
        }
        return getItineraries().ids();
    }

    public void banMembers(UUID loginMemberId, List<UUID> memberIds) {
        this.tripParticipants.banMembers(loginMemberId, memberIds, this);
    }

    public void leave(UUID memberId) {
        if (!this.status.canLeave()) {
            throw new TripNotReadyException();
        }

        TripParticipant participant = tripParticipants.findParticipantById(memberId)
                .orElseThrow(() -> new NotParticipantException("현재 여행에 참여하고 있지 않습니다."));

        if (participant.isLeader()) {
            throw new LeaderCannotLeaveException();
        }
        tripParticipants.removeParticipant(memberId);
    }

    public void delegateLeader(UUID currentLeaderId, UUID newLeaderId) {
        if (this.status != TripStatus.BEFORE_TRIP) {
            throw new TripNotReadyException();
        }
        tripParticipants.delegateLeader(currentLeaderId, newLeaderId);
    }

    public void changeStatusToConfirming() {
        this.status = BEFORE_TRIP;
    }

    public void changeStatusToRecruitmentClosed() {
        this.status = RECRUITMENT_CLOSED;
    }

    public void validateReadyTripStatus() {
        if(this.status != RECRUITMENT_CLOSED){
            throw new BusinessException(NOT_TRIP_READY_STATUS);
        }
    }
}
