package com.retrip.trip.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Itinerary extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            nullable = false,
            columnDefinition = "varbinary(16)",
            foreignKey = @ForeignKey(name = "fk_itinerary_to_trip")
    )
    private Trip trip;

    private Itinerary(String name, Trip trip) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.trip = trip;
    }

    public static Itinerary create(Trip trip, int day) {
        validate(day);
        return new Itinerary("day " + day, trip);
    }

    private static void validate(int day) {
        if (day < 1) {
            throw new RuntimeException();
        }
    }
}
