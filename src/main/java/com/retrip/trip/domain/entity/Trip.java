package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.entity.participant.Participant;
import com.retrip.trip.domain.entity.participant.Participants;
import com.retrip.trip.domain.exception.*;
import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.vo.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_MEMBER_BANNED_CANNOT_APPLY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Trip extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;

    private UUID destinationId;

    @Version private long version;

    @Embedded private TripTitle title;

    @Embedded private TripDescription description;

    private boolean open;

    private int maxParticipants;

    @Column(name = "status", length = 50, nullable = false)
    private TripStatus status;

    @Column(name = "category", length = 50, nullable = false)
    private TripCategory category;

    @Embedded private Participants participants;

    @Embedded private TripDemands tripDemands;

    @Embedded private TripPeriod period;

    @Embedded private Itineraries itineraries;

    public static Trip create(
            UUID destinationId,
            TripTitle title,
            TripDescription description,
            TripPeriod period,
            boolean open,
            int maxParticipants,
            TripCategory category) {
        validateMaxParticipants(maxParticipants);
        Trip trip =
                Trip.builder()
                        .id(UUID.randomUUID())
                        .destinationId(destinationId)
                        .title(title)
                        .description(description)
                        .period(period)
                        .open(open)
                        .category(category)
                        .status(TripStatus.RECRUITING)
                        .maxParticipants(maxParticipants)
                        .tripDemands(new TripDemands())
                        .build();
        return trip;
    }

    public static Trip createWithItineraries(
            UUID destinationId,
            TripTitle title,
            TripDescription description,
            TripPeriod period,
            boolean open,
            int maxParticipants,
            TripCategory category) {
        validateMaxParticipants(maxParticipants);
        Trip trip =
                Trip.builder()
                        .id(UUID.randomUUID())
                        .destinationId(destinationId)
                        .title(title)
                        .description(description)
                        .period(period)
                        .open(open)
                        .category(category)
                        .status(TripStatus.RECRUITING)
                        .maxParticipants(maxParticipants)
                        .build();
        trip.itineraries = new Itineraries(trip, period);
        return trip;
    }

    public void addDemand(TripDemand demand) {
        validateAddDemand(demand);
        this.tripDemands.addDemand(demand);
    }

    private void validateAddDemand(TripDemand demand) {
        if (this.participants.isBan(demand.getMemberId())) {
            throw new BusinessException(TRIP_MEMBER_BANNED_CANNOT_APPLY);
        }
    }

    private static void validateMaxParticipants(int maxParticipants) {
        if (maxParticipants < 1) {
            throw new InvalidValueException(
                    ErrorCode.INVALID_MAX_PARTICIPANTS, "최대 참여 인원은 1명 이상이어야 합니다.");
        }
    }

    public void updatePeriod(TripPeriod period, @NotNull UUID memberId) {
        this.period = period;
        if (Objects.isNull(this.itineraries)) {
            this.itineraries = new Itineraries(this, period);
        } else {
            this.itineraries.updateByPeriod(period, this);
        }
    }

    public List<UUID> getItinerariesIds() {
        if (Objects.isNull(getItineraries())) {
            return List.of();
        }
        return getItineraries().ids();
    }

    public void banMembers(UUID loginMemberId, List<UUID> memberIds) {
        this.participants.banMembers(loginMemberId, memberIds, this);
    }

    public void leave(UUID memberId) {
        if (!this.status.canLeave()) {
            throw new TripNotReadyException();
        }

        Participant participant =
                participants
                        .findParticipantById(memberId)
                        .orElseThrow(() -> new NotParticipantException("현재 여행에 참여하고 있지 않습니다."));

        if (participant.isLeader()) {
            throw new LeaderCannotLeaveException();
        }
        participants.removeParticipant(memberId);
    }

    public void delegateLeader(UUID currentLeaderId, UUID newLeaderId) {
        if (this.status != TripStatus.BEFORE_TRIP) {
            throw new TripNotReadyException();
        }
        participants.delegateLeader(currentLeaderId, newLeaderId);
    }

    public void updateMaxParticipants(int maxParticipants) {
        // todo: 최대 참여자 수 변경 함수 이동
    }
}
