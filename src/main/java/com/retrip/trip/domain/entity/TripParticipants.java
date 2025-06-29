package com.retrip.trip.domain.entity;

import static com.retrip.trip.domain.exception.common.ErrorCode.NOT_TRIP_LEADER;
import static com.retrip.trip.domain.exception.common.ErrorCode.TRIP_MEMBER_NOT_IN_TRIP;
import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.exception.common.BusinessException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
import com.retrip.trip.domain.vo.ParticipantStatus;
import com.retrip.trip.domain.vo.TripStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED, force = true)
public class TripParticipants {
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TripParticipant> values = new ArrayList<>();

    public TripParticipants(UUID leaderId, Trip trip) {
        TripParticipant leader = TripParticipant.createTripLeader(leaderId, trip);
        values.add(leader);
    }

    public void addParticipant(TripParticipant participant) {
        values.add(participant);
    }

    public boolean updatableByLeader(UUID memberId) {
        return isLeader(memberId);
    }

    public boolean isLeader(UUID memberId) {
        return this.values.stream()
                .filter(m -> memberId.equals(m.getMemberId()))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("여행 회원이 아닙니다."))
                .isLeader();
    }

    public void banMembers(UUID loginMemberId, List<UUID> memberIdList, Trip trip) {
        validateTripRecruitingStatus(trip.getStatus());
        validateTripLeader(loginMemberId);
        validateExistParticipantMember(memberIdList);

        List<TripParticipant> participantsToBan = values.stream()
                .filter(m -> memberIdList.contains(m.getMemberId()))
                .toList();

        participantsToBan.forEach(TripParticipant::ban);
    }

    private void validateExistParticipantMember(List<UUID> memberIdList) {
        boolean isAllExist = values.stream()
                .anyMatch(m -> memberIdList.contains(m.getMemberId()));

        if(!isAllExist) {
            throw new BusinessException(TRIP_MEMBER_NOT_IN_TRIP);
        }
    }

    private void validateTripLeader(UUID loginMemberId) {
        if(!isLeader(loginMemberId)) {
            throw new BusinessException(NOT_TRIP_LEADER);
        }
    }

    public void validateTripRecruitingStatus(TripStatus status) {
        if (!TripStatus.RECRUITING.equals(status)) {
            throw new IllegalStateException("해당 여행은 모집 중이 아닙니다.");
        }
    }

    public boolean isBan(UUID memberId) {
        return values.stream()
                .anyMatch(tripParticipant -> memberId.equals(tripParticipant.getMemberId()) &&
                                                          tripParticipant.getStatus() == ParticipantStatus.EXPELLED);
    }

    public boolean anyDuplicate(List<UUID> memberIds) {
        return values.stream()
                .anyMatch(p -> memberIds.contains(p.getMemberId()));
    }
}

