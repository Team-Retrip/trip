package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripInvitationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.retrip.trip.domain.vo.TripInvitationStatus.ACCEPTED;
import static com.retrip.trip.domain.vo.TripInvitationStatus.INVITED;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
@Entity
public class TripInvitation extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            nullable = false,
            columnDefinition = "varbinary(16)",
            foreignKey = @ForeignKey(name = "fk_invitation_to_trip"))
    private Trip trip;
    private UUID memberId;
    private TripInvitationStatus status;

    public TripInvitation(Trip trip, UUID memberId) {
        this.id = UUID.randomUUID();
        this.trip = trip;
        this.memberId = memberId;
        this.status = INVITED;
    }

    public boolean cannotInviteAgain() {
        return this.status == INVITED
                || this.status == ACCEPTED;
    }

    public void inviteAgain() {
        this.status = INVITED;
    }
}
