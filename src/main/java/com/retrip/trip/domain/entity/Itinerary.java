package com.retrip.trip.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
@Entity
public class Itinerary extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;

    private String name;
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            nullable = false,
            columnDefinition = "varbinary(16)",
            foreignKey = @ForeignKey(name = "fk_itinerary_to_trip"))
    private Trip trip;

    @Embedded ItineraryDetails itineraryDetails;

    private Itinerary(String name, Trip trip, LocalDate date) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.trip = trip;
        this.date = date;
    }

    public static Itinerary create(Trip trip, LocalDate date) {
        int day = date.compareTo(trip.getPeriod().getStart()) + 1;
        return create(trip, day, date);
    }

    private static void validate(int day) {
        if (day < 1) {
            throw new IllegalArgumentException("여행 일차는 1보다 작을 수 없습니다.");
        }
    }

    public static Itinerary create(Trip trip, int day, LocalDate date) {
        validate(day);
        return new Itinerary("day " + day, trip, date);
    }
}
