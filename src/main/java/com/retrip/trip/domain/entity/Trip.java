package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripStatus;
import com.retrip.trip.domain.vo.TripTitle;
import jakarta.persistence.*;
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
    private Participants participants;

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
        trip.participants = new Participants(memberId, trip);
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
        trip.participants = new Participants(memberId, trip);
        return trip;
    }

    public void addParticipant(TripParticipant participant) {
        this.participants.addParticipant(participant);
    }
}

