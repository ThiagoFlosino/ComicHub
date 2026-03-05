package com.comichub.collection.infrastructure.adapter;

import com.comichub.collection.domain.model.Collection;
import com.comichub.collection.domain.model.CollectionFilter;
import com.comichub.collection.domain.model.CollectionStatus;
import com.comichub.collection.domain.port.out.CollectionRepository;
import com.comichub.collection.infrastructure.entity.CollectionEntity;
import com.comichub.collection.infrastructure.entity.CollectionId;
import com.comichub.collection.infrastructure.persistence.CollectionProjection;
import com.comichub.collection.infrastructure.persistence.SpringDataCollectionRepository;

import java.util.List;
import java.util.UUID;

public class JpaCollectionRepositoryAdapter implements CollectionRepository {

    private final SpringDataCollectionRepository springDataCollectionRepository;

    public JpaCollectionRepositoryAdapter(SpringDataCollectionRepository springDataCollectionRepository) {
        this.springDataCollectionRepository = springDataCollectionRepository;
    }

    @Override
    public Collection save(Collection collection) {
        var id = new CollectionId(collection.userId(), collection.itemId());
        var entity = new CollectionEntity(id, collection.shelfLocation(), collection.status(), collection.addedAt());
        return toDomain(springDataCollectionRepository.save(entity));
    }

    @Override
    public List<Collection> findByUserIdAndFilter(UUID userId, CollectionFilter filter) {
        String status = filter.status() != null ? filter.status().name() : null;
        String series = filter.series();
        return springDataCollectionRepository.findByFilters(userId, status, series)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private Collection toDomain(CollectionEntity e) {
        return new Collection(
                e.getId().getUserId(),
                e.getId().getItemId(),
                e.getShelfLocation(),
                e.getStatus(),
                e.getAddedAt()
        );
    }

    private Collection toDomain(CollectionProjection p) {
        return new Collection(
                p.getUserId(),
                p.getItemId(),
                p.getShelfLocation(),
                CollectionStatus.valueOf(p.getStatus()),
                p.getAddedAt()
        );
    }
}
