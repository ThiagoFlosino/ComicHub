package com.comichub.collection.infrastructure.rest.dto;

import com.comichub.collection.domain.model.Collection;
import com.comichub.collection.domain.model.CollectionStatus;

import java.time.Instant;
import java.util.UUID;

public record CollectionResponse(
        UUID userId,
        UUID itemId,
        String shelfLocation,
        CollectionStatus status,
        Instant addedAt
) {
    public static CollectionResponse from(Collection c) {
        return new CollectionResponse(c.userId(), c.itemId(), c.shelfLocation(), c.status(), c.addedAt());
    }
}
