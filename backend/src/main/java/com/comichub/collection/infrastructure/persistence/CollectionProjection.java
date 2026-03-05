package com.comichub.collection.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

/**
 * Projeção para queries nativas que fazem JOIN com a tabela items.
 * Usada para filtrar coleções por série (campo que pertence ao catálogo).
 */
public interface CollectionProjection {
    UUID getUserId();
    UUID getItemId();
    String getShelfLocation();
    String getStatus();
    Instant getAddedAt();
}
