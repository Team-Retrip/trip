package com.retrip.trip.domain.entity;

import com.retrip.trip.domain.exception.common.EntityNotFoundException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.Comparator.comparingInt;
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
        reorderAll();
    }

    public void addItineraryDetail(ItineraryDetail itineraryDetail) {
        values.add(itineraryDetail);
    }

    public void addAll(List<ItineraryDetail> details) {
        int baseOrder = values.size();
        IntStream.range(0, details.size())
                .forEach(i -> {
                    details.get(i).updateSortOrder(baseOrder + i);
                    values.add(details.get(i));
                });
    }

    public void updateItineraryDetail(UUID detailId, String memo, LocalDateTime time, UUID locationId) {
        findByItineraryDetail(detailId)
                .update(memo, time, locationId);
    }

    public ItineraryDetail removeForMove(UUID detailId) {
        ItineraryDetail detail = findByItineraryDetail(detailId);
        this.values.remove(detail);
        reorderAll();
        return detail;
    }

    public int nextSortOrder() {
        return values.size();
    }

    public void insertAtOrder(ItineraryDetail detail, int targetSortOrder) {
        int insertAt = Math.min(targetSortOrder, values.size());
        // insertAt 이상인 기존 항목들을 한 칸씩 뒤로 밀기
        values.stream()
                .filter(d -> d.getSortOrder() >= insertAt)
                .forEach(d -> d.updateSortOrder(d.getSortOrder() + 1));
        detail.updateSortOrder(insertAt);
        values.add(detail);
    }

    public void reorder(List<UUID> orderedIds) {
        for (int i = 0; i < orderedIds.size(); i++) {
            findByItineraryDetail(orderedIds.get(i)).updateSortOrder(i);
        }
    }

    private void reorderAll() {
        List<ItineraryDetail> sorted = values.stream()
                .sorted(comparingInt(ItineraryDetail::getSortOrder))
                .toList();
        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).updateSortOrder(i);
        }
    }

    private ItineraryDetail findByItineraryDetail(UUID detailId) {
        return this.values.stream()
                .filter(detail -> detail.getId().equals(detailId))
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);
    }
}