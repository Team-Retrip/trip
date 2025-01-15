package com.retrip.trip.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Itinerary extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;
    private String name;

    private Itinerary(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public static Itinerary create(int day) {
        validate(day);
        return new Itinerary("day " + day);
    }

    private static void validate(int day) {
        if (day < 1) {
            throw new RuntimeException();
        }
    }
}
