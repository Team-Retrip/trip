package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripTitle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Trip extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;
    private UUID leaderId;

    @Version
    private long version;

    @Embedded
    private TripTitle title;
    private UUID destinationId;

    @Embedded
    private TripPeriod period;
    private boolean open;

    @Embedded
    private Itineraries itineraries;

    private Trip(String title, UUID destinationId, TripPeriod period, boolean open, UUID leaderId) {
        this.id = UUID.randomUUID();
        this.title = new TripTitle(title);
        this.destinationId = destinationId;
        this.period = period;
        this.open = open;
        this.leaderId = leaderId;
    }

    public static Trip create(String title, UUID destinationId, TripPeriod period, boolean open, UUID leaderId) {
        return new Trip(title, destinationId, period, open, leaderId);
    }

    public static Trip createWithItineraries(String title, UUID destinationId, TripPeriod period, boolean open, UUID leaderId) {
        Trip trip = new Trip(title, destinationId, period, open, leaderId);
        trip.itineraries = new Itineraries(trip, period);
        return trip;
    }
}
