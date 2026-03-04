package com.comichub.catalog.infrastructure.adapter.googlebooksapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * DTO de resposta da Google Books API.
 * Confinado à camada infrastructure — nunca exposto ao domínio.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record GoogleBooksResponse(
        int totalItems,
        List<VolumeItem> items
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    record VolumeItem(VolumeInfo volumeInfo) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record VolumeInfo(
            String title,
            List<String> authors,
            String publisher,
            String description,
            List<IndustryIdentifier> industryIdentifiers,
            ImageLinks imageLinks
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record IndustryIdentifier(String type, String identifier) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ImageLinks(String thumbnail) {}
}
