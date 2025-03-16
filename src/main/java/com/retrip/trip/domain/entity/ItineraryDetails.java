package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.TripPeriod;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
@Embeddable
public class ItineraryDetails {
    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItineraryDetail> values = new ArrayList<>();

    public ItineraryDetails(List<ItineraryDetail> itineraryDetails) {
        values = itineraryDetails;
    }

    public void clear() {
        if (this.values.isEmpty()) {
            return;
        }
        this.values.clear();
    }


    public void update(ItineraryDetails itineraryDetails) {
        this.values = itineraryDetails.values;
    }
}
