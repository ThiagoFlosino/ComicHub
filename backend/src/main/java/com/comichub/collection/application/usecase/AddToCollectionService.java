package com.comichub.collection.application.usecase;

import com.comichub.collection.domain.model.Collection;
import com.comichub.collection.domain.model.CollectionStatus;
import com.comichub.collection.domain.port.in.AddToCollectionUseCase;
import com.comichub.collection.domain.port.out.CollectionRepository;

import java.time.Instant;
import java.util.UUID;

/**
 * Use Case: adiciona um item ao acervo físico do utilizador.
 * REGRA: Nenhum import de Spring nesta camada.
 */
public class AddToCollectionService implements AddToCollectionUseCase {

    private final CollectionRepository collectionRepository;

    public AddToCollectionService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    @Override
    public Collection add(UUID userId, UUID itemId, String shelfLocation, CollectionStatus status) {
        var collection = new Collection(userId, itemId, shelfLocation, status, Instant.now());
        return collectionRepository.save(collection);
    }
}
