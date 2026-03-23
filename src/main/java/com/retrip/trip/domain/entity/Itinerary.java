package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.common.InvalidValueException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_DAY_MUST_BE_POSITIVE;
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
            throw new InvalidValueException(TRIP_DAY_MUST_BE_POSITIVE);
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
        this.itineraryDetails.addItineraryDetail(itineraryDetail);
    }

    public List<ItineraryDetail> addItineraryDetails(List<ItineraryDetail> details) {
        if (Objects.isNull(this.itineraryDetails)) {
            this.itineraryDetails = new ItineraryDetails();
        }
        this.itineraryDetails.addAll(details);
        return details;
    }

    public void updateItineraryDetail(UUID detailId, String memo, LocalDateTime time, UUID locationId) {
        this.itineraryDetails.updateItineraryDetail(detailId, memo, time, locationId);
    }

    public void reorderItineraryDetails(List<UUID> orderedIds) {
        this.itineraryDetails.reorder(orderedIds);
    }

    public ItineraryDetail removeDetailForMove(UUID detailId) {
        return this.itineraryDetails.removeForMove(detailId);
    }

    public void acceptMovedDetail(ItineraryDetail detail, int targetSortOrder) {
        if (Objects.isNull(this.itineraryDetails)) {
            this.itineraryDetails = new ItineraryDetails();
        }
        detail.moveTo(this, targetSortOrder);
        this.itineraryDetails.insertAtOrder(detail, targetSortOrder);
    }
}