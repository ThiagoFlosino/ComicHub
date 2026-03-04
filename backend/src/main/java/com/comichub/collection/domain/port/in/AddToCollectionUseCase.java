package com.comichub.collection.domain.port.in;

import com.comichub.collection.domain.model.Collection;
import com.comichub.collection.domain.model.CollectionStatus;

import java.util.UUID;

/**
 * Porta de ENTRADA: adiciona um item ao acervo físico do utilizador.
 */
public interface AddToCollectionUseCase {

    Collection add(UUID userId, UUID itemId, String shelfLocation, CollectionStatus status);
}
