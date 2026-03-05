package com.comichub.catalog.infrastructure.persistence;

import com.comichub.catalog.infrastructure.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataItemRepository extends JpaRepository<ItemEntity, UUID> {

    Optional<ItemEntity> findByIsbn(String isbn);

    List<ItemEntity> findBySeries(String series);
}
