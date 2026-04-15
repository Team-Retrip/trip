package com.retrip.trip.domain.entity.demand;

import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.entity.BaseEntity;
import com.retrip.trip.domain.vo.DemandStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED, force = true)
public class Demand extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;

    private UUID tripId;

    private UUID memberId;

    @Column(name = "message")
    private String message;

    @Column(name = "status", length = 50, nullable = false)
    private DemandStatus status;


    public static Demand create(UUID memberId, UUID tripId, String message) {
        return Demand.builder()
                .id(UUID.randomUUID())
                .tripId(tripId)
                .memberId(memberId)
                .message(message)
                .status(DemandStatus.PENDING)
                .build();
    }

    public void setStatus(DemandStatus status) {
        this.status = status;
    }

    public void approve() {
        this.status = DemandStatus.APPROVED;
    }

    public void reject() {
        this.status = DemandStatus.REJECTED;
    }

    public boolean isAlreadyDemanded(UUID memberId, UUID tripId) {
        return memberId.equals(this.memberId) && tripId.equals(this.tripId) && Set.of(DemandStatus.PENDING, DemandStatus.APPROVED).contains(this.status);
    }

    public boolean isNotPendingStatus() {
        return !DemandStatus.PENDING.equals(this.status);
    }

    public boolean isNotOwner(UUID memberId) {
        return !this.memberId.equals(memberId);
    }
}
