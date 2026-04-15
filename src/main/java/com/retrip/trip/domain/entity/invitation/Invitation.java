package com.retrip.trip.domain.entity.invitation;

import com.retrip.trip.domain.entity.BaseEntity;
import com.retrip.trip.domain.vo.InvitationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.retrip.trip.domain.vo.InvitationStatus.*;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
@Entity
public class Invitation extends BaseEntity {
    public static final long INVITATION_EXPIRE_DAYS = 5L;

    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;
    private UUID tripId;
    private UUID memberId;
    private InvitationStatus status;
    private LocalDateTime invitedAt;
    private long expireDays;
    private LocalDateTime expiresAt;

    @Version
    private long version;

    public Invitation(UUID tripId, UUID memberId) {
        this.id = UUID.randomUUID();
        this.tripId = tripId;
        this.memberId = memberId;
        this.status = INVITED;
        this.invitedAt = LocalDateTime.now();
        this.expireDays = INVITATION_EXPIRE_DAYS;
        this.expiresAt = calculateExpiry();
    }

    public boolean cannotInviteAgain() {
        return this.status == INVITED ||
                this.status == ACCEPTED;
    }

    public void inviteAgain() {
        this.status = INVITED;
    }

    public LocalDateTime calculateExpiry() {
        return this.invitedAt
                .toLocalDate()
                .plusDays(5)
                .atStartOfDay();
    }

    public boolean isExpired() {
        return status == EXPIRED ||
                LocalDateTime.now().isAfter(expiresAt);
    }

    public void accept() {
        this.status = ACCEPTED;
    }

    public boolean cannotReject() {
        return this.status != INVITED;
    }

    public void reject() {
        this.status = REJECTED;
    }

    public boolean canDelete() {
        return this.status == REJECTED || this.status == EXPIRED;
    }

    public void expire() {
        this.status = EXPIRED;
    }
}
