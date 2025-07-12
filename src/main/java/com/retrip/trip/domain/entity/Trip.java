package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.PeriodUpdateFailedException;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripStatus;
import com.retrip.trip.domain.vo.TripTitle;
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

import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_MEMBER_BANNED_CANNOT_APPLY;
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

    @Column(name = "max_participants", nullable = false)
    private int maxParticipants;

    @Column(name = "status", length = 50, nullable = false)
    private TripStatus status;

    @Column(name = "category", length = 50, nullable = false)
    private TripCategory category;

    @Embedded
    private TripParticipants tripParticipants;

    @Embedded
    private TripDemands tripDemands;

    @Embedded
    private TripPeriod period;

    @Embedded
    private Itineraries itineraries;

    @Embedded
    private TripInvitations invitations;

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
                .maxParticipants(maxParticipants)
                .category(category)
                .status(TripStatus.RECRUITING)
                .tripDemands(new TripDemands())
                .build();
        trip.tripParticipants = new TripParticipants(memberId, trip);
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
                .maxParticipants(maxParticipants)
                .category(category)
                .status(TripStatus.RECRUITING)
                .build();
        trip.itineraries = new Itineraries(trip, period);
        trip.tripParticipants = new TripParticipants(leaderId, trip);
        trip.invitations = new TripInvitations();
        return trip;
    }

    public void addParticipant(TripParticipant participant) {
        this.tripParticipants.addParticipant(participant);
    }

    public void addDemand(TripDemand demand) {
        validateAddDemand(demand);
        this.tripDemands.addDemand(demand);
    }

    private void validateAddDemand(TripDemand demand) {
        if(this.tripParticipants.isBan(demand.getMemberId())){
            throw new BusinessException(TRIP_MEMBER_BANNED_CANNOT_APPLY);
        }
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

    public void createInvitations(UUID leaderId, List<UUID> memberIds) {
        invitations.add(this, leaderId, memberIds, tripParticipants);
    }
}

