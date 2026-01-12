package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.common.EntityNotFoundException;
import com.retrip.trip.domain.exception.common.InvalidValueException;
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

import static com.retrip.trip.domain.exception.common.ErrorCode.ITINERARY_DATE_MISMATCH;
import static com.retrip.trip.domain.exception.common.ErrorCode.ITINERARY_TIME_DUPLICATED;
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

    public void updateItineraryDetail(ItineraryDetail itineraryDetail, LocalDate baseDay, UUID updateId) {
        ItineraryDetail updatedItineraryDetail = findByItineraryDetail(updateId);
        updatedItineraryDetail.update(itineraryDetail);
        this.values.remove(updatedItineraryDetail);

        validate(itineraryDetail.getTime(), baseDay);
        values.add(updatedItineraryDetail);
    }

    private ItineraryDetail findByItineraryDetail(UUID updateId) {
        return this.values.stream().filter(id -> id.getId().equals(updateId)).findFirst().orElseThrow(EntityNotFoundException::new);
    }

    private void validate(ItineraryDetailTime time, LocalDate baseDay) {
        if (!baseDay.equals(time.getValue().toLocalDate())) {
            throw new InvalidValueException(ITINERARY_DATE_MISMATCH);
        }
        if (this.values.stream().anyMatch(id -> id.getTime().equals(time))) {
            throw new InvalidValueException(ITINERARY_TIME_DUPLICATED);
        }
    }

}
