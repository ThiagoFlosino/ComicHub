package com.comichub.collection.domain.port.in;

import com.comichub.collection.domain.model.Collection;
import com.comichub.collection.domain.model.CollectionFilter;

import java.util.List;
import java.util.UUID;

/**
 * Porta de ENTRADA: lista o acervo de um utilizador com filtros opcionais.
 */
public interface ListCollectionUseCase {

    List<Collection> list(UUID userId, CollectionFilter filter);
}
