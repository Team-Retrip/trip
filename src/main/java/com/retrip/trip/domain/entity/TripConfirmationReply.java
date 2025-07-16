package com.retrip.trip.domain.entity;

import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.vo.ConfirmationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class TripConfirmationReply extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_confirmation_demand_id",
            nullable = false,
            columnDefinition = "varbinary(16)",
            foreignKey = @ForeignKey(name = "fk_confirmation_reply_to_confirmation_demand")
    )
    private TripConfirmationDemand confirmationDemand;

    private UUID memberId;

    @Enumerated(EnumType.STRING)
    private ConfirmationStatus status;

    public static TripConfirmationReply create(TripConfirmationDemand demand, UUID memberId) {
        return TripConfirmationReply.builder()
                .id(UUID.randomUUID())
                .confirmationDemand(demand)
                .memberId(memberId)
                .status(ConfirmationStatus.PENDING)
                .build();
    }

    public boolean isAccepted() {
        return this.status == ConfirmationStatus.ACCEPTED;
    }

    public void accept() {
        this.status = ConfirmationStatus.ACCEPTED;
    }

    public void reject() {
        this.status = ConfirmationStatus.REJECTED;
    }

    public void pending() {
        this.status = ConfirmationStatus.PENDING;
    }
}


