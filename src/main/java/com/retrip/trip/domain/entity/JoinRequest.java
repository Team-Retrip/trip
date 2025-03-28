package com.retrip.trip.domain.entity;

import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.vo.ParticipantRole;
import com.retrip.trip.domain.vo.ParticipantStatus;
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
import lombok.Setter;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED, force = true)
public class JoinRequest extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;
    private UUID memberId;

    @Column(name = "message")
    private String message;

    @Column(name = "status", length = 50, nullable = false)
    private ParticipantStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            nullable = false,
            columnDefinition = "varbinary(16)",
            foreignKey = @ForeignKey(name = "fk_trip_participant_to_trip")
    )
    private Trip trip;

    public static JoinRequest create(UUID userId, Trip trip, String message) {
        return new JoinRequest(UUID.randomUUID(), userId, message, ParticipantStatus.PENDING, trip);
    }

    public void setStatus(ParticipantStatus status) {
        this.status = status;
    }

    public void ensurePending() {
        if (!this.status.equals(ParticipantStatus.PENDING)) {
            throw new IllegalStateException("참여 요청의 상태가 '대기' 상태가 아닙니다.");
        }
    }

    public TripParticipant approve() {
        ensurePending();
        this.status = ParticipantStatus.APPROVED;
        TripParticipant participant = TripParticipant.createTripParticipant(memberId, trip);
        trip.addParticipant(participant);
        return participant;
    }

    public void reject() {
        ensurePending(); // 상태가 대기 상태인지 검증
        this.status = ParticipantStatus.REJECTED;
    }
}
