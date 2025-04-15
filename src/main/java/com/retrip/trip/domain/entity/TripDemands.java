package com.retrip.trip.domain.entity;

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
        values.add(demand);
    }
}
