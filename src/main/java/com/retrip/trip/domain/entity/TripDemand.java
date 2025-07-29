package com.retrip.trip.domain.entity;

import static com.retrip.trip.domain.exception.common.ErrorCode.NOT_TRIP_LEADER;
import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.vo.TripDemandStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED, force = true)
public class TripDemand extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;

    private UUID memberId;

    @Column(name = "message")
    private String message;

    @Column(name = "status", length = 50, nullable = false)
    private TripDemandStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            nullable = false,
            columnDefinition = "varbinary(16)",
            foreignKey = @ForeignKey(name = "fk_trip_demand_to_trip"))
    private Trip trip;

    public static TripDemand create(UUID memberId, Trip trip, String message) {
        return new TripDemand(UUID.randomUUID(), memberId, message, TripDemandStatus.PENDING, trip);
    }

    public void approve() {
        ensurePending();
        this.status = TripDemandStatus.APPROVED;
    }

    public void reject() {
        ensurePending();
        this.status = TripDemandStatus.REJECTED;
    }

    public void ensurePending() {
        if (!TripDemandStatus.PENDING.equals(this.status)) {
            throw new IllegalStateException("참여 요청의 상태가 '대기' 상태가 아닙니다.");
        }
    }
}
