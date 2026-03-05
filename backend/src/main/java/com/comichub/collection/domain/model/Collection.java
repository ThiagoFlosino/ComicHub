package com.comichub.collection.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidade de domínio: item na estante física do utilizador.
 * REGRA: Nenhum import de Spring, JPA ou framework externo neste pacote.
 */
public record Collection(
        UUID userId,
        UUID itemId,
        String shelfLocation,
        CollectionStatus status,
        Instant addedAt
) {}
