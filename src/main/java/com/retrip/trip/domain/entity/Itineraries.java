package com.retrip.trip.domain.entity;

import static lombok.AccessLevel.PROTECTED;

import com.retrip.trip.domain.vo.TripPeriod;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
@Embeddable
public class Itineraries {

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Itinerary> values = new ArrayList<>();

    public Itineraries(TripPeriod period, List<Itinerary> itineraries) {
        List<LocalDate> dates = getDates(itineraries);
        validate(period, dates);
        this.values.addAll(itineraries);
    }

    private List<LocalDate> getDates(List<Itinerary> itineraries) {
        return itineraries.stream().map(Itinerary::getDate).toList();
    }

    private void validate(TripPeriod period, List<LocalDate> dates) {
        if (dates.stream().anyMatch(period::isNotInclude)) {
            throw new IllegalArgumentException("일정의 날짜는 여행 기간을 벗어날 수 없습니다.");
        }
    }

    public void update(Itineraries itineraries) {
        values.addAll(itineraries.values);
    }

    public void clear() {
        if (this.values.isEmpty()) {
            return;
        }
        this.values.stream()
            .filter(Objects::nonNull)
            .forEach(
                itinerary -> {
                    if (itinerary.itineraryDetails != null) {
                        itinerary.itineraryDetails.clear();
                    }
                });
        this.values.clear();
    }
}
