package com.comichub.collection.infrastructure.entity;

import com.comichub.collection.domain.model.CollectionStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "collections")
public class CollectionEntity {

    @EmbeddedId
    private CollectionId id;

    private String shelfLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollectionStatus status;

    private Instant addedAt;

    protected CollectionEntity() {}

    public CollectionEntity(CollectionId id, String shelfLocation, CollectionStatus status, Instant addedAt) {
        this.id = id;
        this.shelfLocation = shelfLocation;
        this.status = status;
        this.addedAt = addedAt;
    }

    public CollectionId getId()           { return id; }
    public String getShelfLocation()      { return shelfLocation; }
    public CollectionStatus getStatus()   { return status; }
    public Instant getAddedAt()           { return addedAt; }
}
