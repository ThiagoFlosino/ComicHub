package com.comichub.collection.infrastructure.rest.dto;

import com.comichub.collection.domain.model.CollectionStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddToCollectionRequest(
        @NotNull UUID itemId,
        String shelfLocation,
        CollectionStatus status
) {}
