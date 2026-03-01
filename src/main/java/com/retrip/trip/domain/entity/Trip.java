package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.*;
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

    @Column(name = "image_url")
    private String imageUrl;

    private boolean open;

    @Embedded
    private TripPassword tripPassword;

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private TripStatus status;

    @Column(name = "category", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private TripCategory category;

    @Embedded
    private TripParticipants tripParticipants;

    @Embedded
    private TripConfirmationDemands tripConfirmationDemands;

    @Embedded
    private TripPeriod period;

    @Embedded
    private Itineraries itineraries;

    @Embedded
    private TripHashTags hashTags;

    public static Trip create(
            UUID memberId,
            UUID destinationId,
            TripTitle title,
            String imageUrl,
            TripDescription description,
            TripPeriod period,
            boolean open,
            int maxParticipants,
            List<String> hashTags,
            TripCategory category,
            TripStatus status
    ) {
        Trip trip = Trip.builder()
                .id(UUID.randomUUID())
                .destinationId(destinationId)
                .title(title)
                .imageUrl(imageUrl)
                .description(description)
                .period(period)
                .open(open)
                .category(category)
                .status(status)
                .build();
        trip.tripParticipants = new TripParticipants(memberId, trip, maxParticipants);
        trip.hashTags = new TripHashTags(trip, hashTags);
        return trip;
    }

    public static Trip createWithItineraries(
            UUID leaderId,
            UUID destinationId,
            TripTitle title,
            String imageUrl,
            TripDescription description,
            TripPeriod period,
            boolean open,
            int maxParticipants,
            List<String> hashTags,
            TripCategory category,
            TripStatus status
    ) {
        Trip trip = Trip.builder()
                .id(UUID.randomUUID())
                .destinationId(destinationId)
                .title(title)
                .imageUrl(imageUrl)
                .description(description)
                .period(period)
                .open(open)
                .category(category)
                .status(status)
                .build();
        trip.itineraries = new Itineraries(trip, period);
        trip.tripParticipants = new TripParticipants(leaderId, trip, maxParticipants);
        trip.hashTags = new TripHashTags(trip, hashTags);
        return trip;
    }

    public void addParticipant(TripParticipant participant) {
        this.tripParticipants.addParticipant(participant);
    }

    public void updatePeriod(TripPeriod period, @NotNull UUID memberId) {
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
                .orElseThrow(NotParticipantException::new);

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
        if (this.status != RECRUITMENT_CLOSED) {
            throw new BusinessException(NOT_TRIP_READY_STATUS);
        }
    }

    public void assignPassword(TripPassword password) {
        this.tripPassword = password;
    }

    public void updateVisibility(boolean isOpen) {
        this.open = isOpen;
    }

    public boolean isNotTripRecruitingStatus() {
        return !TripStatus.RECRUITING.equals(status);
    }


    public void update(
            UUID memberId,
            UUID destinationId,
            TripTitle tripTitle,
            TripDescription tripDescription,
            TripHashTags tripHashTags,
            Integer maxParticipants,
            String imageUrl,
            TripCategory category
    ) {
        if (!tripParticipants.updatableByLeader(memberId)) {
            throw new TripUpdateFailedException();
        }

        if (destinationId != null) {
            this.destinationId = destinationId;
        }

        if (tripTitle != null) {
            this.title = tripTitle;
        }
        if (tripDescription != null) {
            this.description = tripDescription;
        }

        if (tripHashTags != null) {
            this.hashTags.getValues().clear();
            this.hashTags.getValues().addAll(tripHashTags.getValues());
        }

        if (maxParticipants != null) {
            this.tripParticipants.updateMaxParticipants(maxParticipants, memberId);
        }
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
        if (category != null) {
            this.category = category;
        }
    }
}
