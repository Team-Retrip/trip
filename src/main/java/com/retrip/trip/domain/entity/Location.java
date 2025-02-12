package com.retrip.trip.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Location extends BaseEntity {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;

    @Version
    private long version;
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id",
            columnDefinition = "varbinary(16)",
            foreignKey = @ForeignKey(name = "fk_location_to_parent"))
    private Location parent;

    public Location(String name, Location parent) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.parent = parent;
    }
}
