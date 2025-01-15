package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripPeriod;
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
    private String title;
    private UUID destinationId;

    @Embedded
    private TripPeriod period;
    private boolean open;

    @Embedded
    private Itineraries Itineraries;

    private Trip(String title, UUID destinationId, TripPeriod period, boolean open, UUID leaderId) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.destinationId = destinationId;
        this.period = period;
        this.open = open;
        this.leaderId = leaderId;
    }

    public static Trip createWithItinerary(String title, UUID destinationId, TripPeriod period, boolean open, UUID leaderId) {
        Trip trip = new Trip(title, destinationId, period, open, leaderId);
        trip.Itineraries = new Itineraries(trip, period);
        return trip;
    }
}
