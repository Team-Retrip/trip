package com.retrip.trip.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;
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

    @Embedded
    private ItineraryDetails itineraryDetails;

    private Itinerary(String name, Trip trip, LocalDate date) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.trip = trip;
        this.date = date;
    }

    public static Itinerary create(Trip trip, int day, LocalDate date) {
        validate(day);
        return new Itinerary("day " + day, trip, date);
    }

    private static void validate(int day) {
        if (day < 1) {
            throw new IllegalArgumentException("여행 일차는 1보다 작을 수 없습니다.");
        }
    }

    public void updateDate(int n) {
        validate(n);
        this.name = "day " + n;
    }

    public void removeAllItineraries() {
        if (!Objects.isNull(this.itineraryDetails)) {
            this.itineraryDetails.removeAll();
        }
    }

    public void removeItineraryDetail(UUID itineraryDetailsId) {
        if (!Objects.isNull(this.itineraryDetails)) {
            this.itineraryDetails.remove(itineraryDetailsId);
        }
    }

    public void addItineraryDetail(ItineraryDetail itineraryDetail) {
        if (Objects.isNull(this.itineraryDetails)) {
            this.itineraryDetails = new ItineraryDetails();
        }
        this.getItineraryDetails().addItineraryDetail(itineraryDetail, this.date);
    }

    public void updateItineraryDetail(ItineraryDetail itineraryDetail, UUID updateId) {
        this.getItineraryDetails().updateItineraryDetail(itineraryDetail, this.date, updateId);
    }
}
