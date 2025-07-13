package com.retrip.trip.domain.entity;

import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.vo.ParticipantRole;
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
public class TripParticipant extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;
    private UUID memberId;

    @Column(name = "role", length = 50, nullable = false)
    private ParticipantRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            nullable = false,
            columnDefinition = "varbinary(16)",
            foreignKey = @ForeignKey(name = "fk_trip_participant_to_trip")
    )
    private Trip trip;

    public static TripParticipant createTripLeader(UUID memberId, Trip trip) {
        return new TripParticipant(
                UUID.randomUUID(),
                memberId,
                ParticipantRole.LEADER,
                trip
        );
    }

    public static TripParticipant createTripParticipant(UUID memberId, Trip trip) {
        return new TripParticipant(
                UUID.randomUUID(),
                memberId,
                ParticipantRole.PARTICIPANT,
                trip
        );
    }

    public boolean isLeader() {
        return this.role.isLeader();
    }

    public void changeRole(ParticipantRole newRole) {
        this.role = newRole;
    }
}