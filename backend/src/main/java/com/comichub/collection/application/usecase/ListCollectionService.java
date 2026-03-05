package com.comichub.collection.application.usecase;

import com.comichub.collection.domain.model.Collection;
import com.comichub.collection.domain.model.CollectionFilter;
import com.comichub.collection.domain.port.in.ListCollectionUseCase;
import com.comichub.collection.domain.port.out.CollectionRepository;

import java.util.List;
import java.util.UUID;

/**
 * Use Case: lista o acervo do utilizador com filtros opcionais.
 * REGRA: Nenhum import de Spring nesta camada.
 */
public class ListCollectionService implements ListCollectionUseCase {

    private final CollectionRepository collectionRepository;

    public ListCollectionService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    @Override
    public List<Collection> list(UUID userId, CollectionFilter filter) {
        return collectionRepository.findByUserIdAndFilter(userId, filter);
    }
}
