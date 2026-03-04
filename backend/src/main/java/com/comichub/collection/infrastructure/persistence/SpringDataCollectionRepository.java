package com.comichub.collection.infrastructure.persistence;

import com.comichub.collection.infrastructure.entity.CollectionEntity;
import com.comichub.collection.infrastructure.entity.CollectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataCollectionRepository extends JpaRepository<CollectionEntity, CollectionId> {

    /**
     * Lista o acervo de um utilizador com filtros opcionais de status e série.
     * Faz JOIN com a tabela items para suportar o filtro por série.
     * Parâmetros nulos desativam o respectivo filtro.
     */
    @Query(nativeQuery = true, value = """
            SELECT c.user_id, c.item_id, c.shelf_location, c.status, c.added_at
            FROM collections c
            LEFT JOIN items i ON c.item_id = i.id
            WHERE c.user_id = :userId
              AND (:status IS NULL OR c.status = :status)
              AND (:series IS NULL OR i.series = :series)
            ORDER BY c.added_at DESC
            """)
    List<CollectionProjection> findByFilters(
            @Param("userId") UUID userId,
            @Param("status") String status,
            @Param("series") String series);
}
