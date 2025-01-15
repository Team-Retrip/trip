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

    private Trip(String title, UUID destinationId, TripPeriod period, boolean open) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.destinationId = destinationId;
        this.period = period;
        this.open = open;
    }

    public static Trip createWithItinerary(String title, UUID destinationId, TripPeriod period, boolean open) {
        Trip trip = new Trip(title, destinationId, period, open);
        trip.Itineraries = new Itineraries(period);
        return trip;
    }

    public void registerLeader(UUID leaderId) {
        this.leaderId = leaderId;
    }
}
