package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.PeriodUpdateFailedException;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripStatus;
import com.retrip.trip.domain.vo.TripTitle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

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
                .build();
        trip.tripParticipants = new TripParticipants(memberId, trip);
        return trip;
    }

    public static Trip createWithItineraries(
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
                .build();
        trip.itineraries = new Itineraries(trip, period);
        trip.tripParticipants = new TripParticipants(memberId, trip);
        return trip;
    }

    public void addParticipant(TripParticipant participant) {
        this.tripParticipants.addParticipant(participant);
    }

    public void validateTripRecruitingStatus() {
        if (!this.getStatus().equals(TripStatus.RECRUITING)) {
            throw new IllegalStateException("해당 여행은 모집 중이 아닙니다.");
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
}

