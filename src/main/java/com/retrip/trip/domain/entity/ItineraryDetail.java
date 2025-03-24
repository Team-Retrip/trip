package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.ItineraryDetailDescription;
import com.retrip.trip.domain.vo.ItineraryDetailPrice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

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
    private UUID locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "itinerary_id",
            nullable = false,
            columnDefinition = "varbinary(16)",
            foreignKey = @ForeignKey(name = "fk_itinerary_detail_to_itinerary"))
    private Itinerary itinerary;

    private ItineraryDetail(Long price, String description, Itinerary itinerary, UUID locationId) {
        this.id = UUID.randomUUID();
        this.price = new ItineraryDetailPrice(price);
        this.description = new ItineraryDetailDescription(description);
        this.locationId = locationId;
        this.itinerary = itinerary;
    }

    private static void validate(Long price, String description) {
        if (price != null && price < 0) {
            throw new IllegalArgumentException("금액은 0보다 작을 수 없습니다.");
        }
        if (description != null && description.length() > 50) {
            throw new IllegalArgumentException("여행 일정 상세 내용은 50자 이내여야 합니다.");
        }
    }

    public static ItineraryDetail create(
            Long price, String description, Itinerary itinerary, UUID locationId) {
        validate(price, description);
        return new ItineraryDetail(price, description, itinerary, locationId);
    }
}
