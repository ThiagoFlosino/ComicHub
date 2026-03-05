package com.comichub.catalog.domain.port.out;

import com.comichub.catalog.domain.model.ComicBook;
import com.comichub.catalog.domain.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de SAÍDA (driven port) — persistência de itens do catálogo.
 * REGRA: Nenhum import de Spring ou JPA neste pacote.
 */
public interface ItemRepository {

    /** Find-or-create idempotente por ISBN. */
    Item save(ComicBook comicBook);

    Optional<Item> findById(UUID id);

    Optional<Item> findByIsbn(String isbn);

    List<Item> findBySeries(String series);
}
