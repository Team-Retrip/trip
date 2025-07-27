package com.retrip.trip.domain.entity.participant;

import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.entity.BaseEntity;
import com.retrip.trip.domain.entity.Trip;
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

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED, force = true)
public class Participant extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;

    private UUID tripId;

    private UUID memberId;

    @Column(name = "role", length = 50, nullable = false)
    private ParticipantRole role;

    @Column(name = "status", length = 50, nullable = false)
    private ParticipantStatus status;

    public Participant(UUID tripId, UUID memberId, ParticipantRole role) {
        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.tripId = tripId;
        this.role = role;
        this.status = ParticipantStatus.ACTIVE;
    }

    public static Participant createTripParticipant(UUID memberId, Trip trip) {
        return new Participant(
                UUID.randomUUID(),
                null,
                memberId,
                ParticipantRole.PARTICIPANT,
                ParticipantStatus.ACTIVE,
                trip);
    }

    public boolean isLeader() {
        return this.role.isLeader();
    }

    public void ban() {
        this.status = ParticipantStatus.EXPELLED;
    }

    public void changeRole(ParticipantRole newRole) {
        this.role = newRole;
    }

    public static Participant create(UUID tripId, UUID memberId, ParticipantRole role) {
        return new Participant(tripId, memberId, role);
    }
}
