package com.retrip.trip.domain.entity;

import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.vo.ItineraryDetailMemo;
import com.retrip.trip.domain.vo.ItineraryDetailTime;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
@Entity
public class ItineraryDetail extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;

    @Embedded
    private ItineraryDetailMemo memo;

    @Embedded
    private ItineraryDetailTime time;

    private int sortOrder;

    private UUID locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "itinerary_id",
            nullable = false,
            columnDefinition = "varbinary(16)",
            foreignKey = @ForeignKey(name = "fk_itinerary_detail_to_itinerary"))
    private Itinerary itinerary;

    private ItineraryDetail(String memo, LocalDateTime time, Itinerary itinerary, UUID locationId, int sortOrder) {
        this.id = UUID.randomUUID();
        this.memo = new ItineraryDetailMemo(memo);
        this.time = new ItineraryDetailTime(time);
        this.locationId = locationId;
        this.itinerary = itinerary;
        this.sortOrder = sortOrder;
    }

    public static ItineraryDetail create(String memo, LocalDateTime time, Itinerary itinerary, UUID locationId, int sortOrder) {
        return new ItineraryDetail(memo, time, itinerary, locationId, sortOrder);
    }

    public void update(String memo, LocalDateTime time, UUID locationId) {
        if (memo != null) this.memo = new ItineraryDetailMemo(memo);
        if (time != null) this.time = new ItineraryDetailTime(time);
        if (locationId != null) this.locationId = locationId;
    }

    public void moveTo(Itinerary target, int newSortOrder) {
        this.itinerary = target;
        this.sortOrder = newSortOrder;
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getMemoValue() {
        return memo != null ? memo.getValue() : null;
    }

    public LocalDateTime getTimeValue() {
        return time != null ? time.getValue() : null;
    }
}