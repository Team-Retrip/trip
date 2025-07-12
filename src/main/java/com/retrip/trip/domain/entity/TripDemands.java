package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class TripDemands {
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripDemand> values = new ArrayList<>();

    public TripDemands(List<TripDemand> values) {
        this.values = values;
    }

    public void addDemand(TripDemand demand) {
        validateTripRecruitingStatus(demand.getTrip().getStatus());
        values.add(demand);
    }

    private void validateTripRecruitingStatus(TripStatus status) {
        if (!TripStatus.RECRUITING.equals(status)) {
            throw new IllegalStateException("해당 여행은 모집 중이 아닙니다.");
        }
    }
}
