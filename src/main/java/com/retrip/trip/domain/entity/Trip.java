package com.retrip.trip.domain.entity;

import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripDescription;
import com.retrip.trip.domain.vo.TripPeriod;
import com.retrip.trip.domain.vo.TripStatus;
import com.retrip.trip.domain.vo.TripTitle;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Trip extends BaseEntity {

    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;

    private UUID destinationId;

    @Version
    private long version;

    @Embedded
    private TripTitle title;

    @Embedded
    private TripDescription description;

    private boolean open;

    @Column(name = "max_participants", nullable = false)
    private int maxParticipants;

    @Column(name = "status", length = 50, nullable = false)
    private TripStatus status;

    @Column(name = "category", length = 50, nullable = false)
    private TripCategory category;

    @Embedded
    private Participants participants;

    @Embedded
    private TripPeriod period;

    @Embedded
    private Itineraries itineraries;

    public static Trip create(
            UUID memberId,
            UUID destinationId,
            TripTitle title,
            TripDescription description,
            TripPeriod period,
            boolean open,
            int maxParticipants,
            TripCategory category) {
        Trip trip =
                Trip.builder()
                        .id(UUID.randomUUID())
                        .destinationId(destinationId)
                        .title(title)
                        .description(description)
                        .period(period)
                        .open(open)
                        .maxParticipants(maxParticipants)
                        .category(category)
                        .itineraries(new Itineraries())
                        .status(TripStatus.RECRUITING)
                        .build();
        trip.participants = new Participants(memberId, trip);
        return trip;
    }

    public void updateItineraries(List<Itinerary> itineraries, UUID updateId) {
        this.participants.update(updateId);
        this.itineraries.clear();
        this.itineraries.update(new Itineraries(period, itineraries));
    }

    public void updatePeriod(TripPeriod period, UUID updateId) {
        this.participants.update(updateId);
        this.period = period;
        this.itineraries.clear();
        // this.itineraries = new Itineraries(this, period); // 기존 일정 남겨둘지 논의
    }
}
