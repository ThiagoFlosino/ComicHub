package com.comichub.collection.domain.port.out;

import com.comichub.collection.domain.model.Collection;
import com.comichub.collection.domain.model.CollectionFilter;

import java.util.List;
import java.util.UUID;

/**
 * Porta de SAÍDA: persistência das coleções.
 */
public interface CollectionRepository {

    Collection save(Collection collection);

    List<Collection> findByUserIdAndFilter(UUID userId, CollectionFilter filter);
}
