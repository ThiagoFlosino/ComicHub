package com.comichub.catalog.domain.port.out;

import com.comichub.catalog.domain.model.ComicBook;

import java.util.Optional;

/**
 * Porta de SAÍDA (driven port) do domínio de Catálogo.
 * Define o contrato que o núcleo de negócio usa para buscar metadados externos.
 * A implementação concreta fica na camada infrastructure.
 */
public interface FetchBookMetadataPort {

    Optional<ComicBook> fetchByIsbn(String isbn);
}
