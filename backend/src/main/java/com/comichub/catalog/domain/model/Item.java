package com.comichub.catalog.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa um item persistido no catálogo (espelho da tabela items).
 * REGRA: Nenhum import de Spring, JPA ou qualquer framework externo neste pacote.
 */
public record Item(
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
) {}
