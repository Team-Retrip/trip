package com.retrip.trip.domain.entity;

import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.vo.ItineraryDetailDescription;
import com.retrip.trip.domain.vo.ItineraryDetailPrice;
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
    private ItineraryDetailPrice price;
    @Embedded
    private ItineraryDetailDescription description;
    @Embedded
    private ItineraryDetailTime time;
    private UUID locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "itinerary_id",
            nullable = false,
            columnDefinition = "varbinary(16)",
            foreignKey = @ForeignKey(name = "fk_itinerary_detail_to_itinerary"))
    private Itinerary itinerary;

    private ItineraryDetail(Long price, String description, LocalDateTime time, Itinerary itinerary, UUID locationId) {
        this.id = UUID.randomUUID();
        this.price = new ItineraryDetailPrice(price);
        this.description = new ItineraryDetailDescription(description);
        this.time = new ItineraryDetailTime(time);
        this.locationId = locationId;
        this.itinerary = itinerary;
    }

    public static ItineraryDetail create(
            Long price, String description, LocalDateTime time, Itinerary itinerary, UUID locationId) {
        return new ItineraryDetail(price, description, time, itinerary, locationId);
    }

    public void update(ItineraryDetail itineraryDetail) {
        this.price = itineraryDetail.getPrice();
        this.description = itineraryDetail.getDescription();
        this.time = itineraryDetail.getTime();
        this.itinerary = itineraryDetail.getItinerary();
        this.locationId = itineraryDetail.getLocationId();
    }
}
