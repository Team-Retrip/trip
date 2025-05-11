package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.vo.ItineraryDetailTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED, force = true)
@Embeddable
public class ItineraryDetails {
    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItineraryDetail> values = new ArrayList<>();

    public void removeAll() {
        values.clear();
    }

    public void remove(UUID itineraryDetailsId) {
        this.values.removeIf(it -> it.getId().equals(itineraryDetailsId));
    }

    public void addItineraryDetail(ItineraryDetail itineraryDetail, LocalDate baseDay) {
        validate(itineraryDetail.getTime(), baseDay);
        values.add(itineraryDetail);
    }

    private void validate(ItineraryDetailTime time, LocalDate baseDay) {
        if (!baseDay.equals(time.getValue().toLocalDate())) {
            throw new IllegalArgumentException("일정과 상세 일정 일자가 다릅니다.");
        }
        if (this.values.stream().anyMatch(id -> id.getTime().equals(time))) {
            throw new IllegalArgumentException("해당 시간에는 이미 상세 일정이 있습니다.");
        }
    }
}
