package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.*;
import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.InvalidValueException;
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

    @Version private long version;

    @Embedded private TripTitle title;

    @Embedded private TripDescription description;

    private boolean open;

    private int maxParticipants;

    @Column(name = "status", length = 50, nullable = false)
    private TripStatus status;

    @Column(name = "category", length = 50, nullable = false)
    private TripCategory category;

    @Embedded private TripDemands tripDemands;

    @Embedded private TripPeriod period;

    @Embedded private Itineraries itineraries;

    public static Trip create(
            UUID destinationId,
            TripTitle title,
            TripDescription description,
            TripPeriod period,
            boolean open,
            int maxParticipants,
            TripCategory category) {
        validateMaxParticipants(maxParticipants);
        Trip trip =
                Trip.builder()
                        .id(UUID.randomUUID())
                        .destinationId(destinationId)
                        .title(title)
                        .description(description)
                        .period(period)
                        .open(open)
                        .category(category)
                        .status(TripStatus.RECRUITING)
                        .maxParticipants(maxParticipants)
                        .tripDemands(new TripDemands())
                        .build();
        return trip;
    }

    public static Trip createWithItineraries(
            UUID destinationId,
            TripTitle title,
            TripDescription description,
            TripPeriod period,
            boolean open,
            int maxParticipants,
            TripCategory category) {
        validateMaxParticipants(maxParticipants);
        Trip trip =
                Trip.builder()
                        .id(UUID.randomUUID())
                        .destinationId(destinationId)
                        .title(title)
                        .description(description)
                        .period(period)
                        .open(open)
                        .category(category)
                        .status(TripStatus.RECRUITING)
                        .maxParticipants(maxParticipants)
                        .build();
        trip.itineraries = new Itineraries(trip, period);
        return trip;
    }

    public void addDemand(TripDemand demand) {
        this.tripDemands.addDemand(demand);
    }

    private static void validateMaxParticipants(int maxParticipants) {
        if (maxParticipants < 1) {
            throw new InvalidValueException(
                    ErrorCode.INVALID_MAX_PARTICIPANTS, "최대 참여 인원은 1명 이상이어야 합니다.");
        }
    }

    public void updatePeriod(TripPeriod period, @NotNull UUID memberId) {
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

    public void canLeave() {
        if (!this.status.canLeave()) {
            throw new TripNotReadyException();
        }
    }

    public void canDelegateLeader() {
        if (this.status != TripStatus.BEFORE_TRIP) {
            throw new TripNotReadyException();
        }
    }

    public void updateMaxParticipants(int maxParticipants) {
        validateMaxParticipants(maxParticipants);
        this.maxParticipants = maxParticipants;
    }

    public void canBan() {
        if (!TripStatus.RECRUITING.equals(status)) {
            throw new IllegalStateException("해당 여행은 모집 중이 아닙니다.");
        }
    }
}
