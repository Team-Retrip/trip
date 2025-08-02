package com.retrip.trip.domain.entity.participant;

import com.retrip.trip.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

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

    public Participant(UUID tripId, UUID memberId) {
        this.id = UUID.randomUUID();
        this.tripId = tripId;
        this.memberId = memberId;
    }
}
