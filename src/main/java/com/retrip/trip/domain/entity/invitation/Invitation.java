package com.retrip.trip.domain.entity.invitation;

import com.retrip.trip.domain.entity.BaseEntity;
import com.retrip.trip.domain.vo.InvitationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.retrip.trip.domain.vo.InvitationStatus.ACCEPTED;
import static com.retrip.trip.domain.vo.InvitationStatus.INVITED;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
@Entity
public class Invitation extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;
    private UUID tripId;
    private UUID memberId;
    private InvitationStatus status;

    public Invitation(UUID tripId, UUID memberId) {
        this.id = UUID.randomUUID();
        this.tripId = tripId;
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
