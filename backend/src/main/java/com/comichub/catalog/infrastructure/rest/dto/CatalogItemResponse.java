package com.comichub.catalog.infrastructure.rest.dto;

import com.comichub.catalog.domain.model.Item;

import java.time.Instant;
import java.util.UUID;

public record CatalogItemResponse(
        UUID id,
        String isbn,
        String title,
        String author,
        String publisher,
        String synopsis,
        String series,
        Integer volume,
        String variant,
        String coverImage,
        Instant createdAt
) {
    public static CatalogItemResponse from(Item item) {
        return new CatalogItemResponse(
                item.id(),
                item.isbn(),
                item.title(),
                item.author(),
                item.publisher(),
                item.synopsis(),
                item.series(),
                item.volume(),
                item.variant(),
                item.coverImage(),
                item.createdAt()
        );
    }
}
