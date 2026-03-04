package com.comichub.catalog.domain.port.in;

import com.comichub.catalog.domain.model.ComicBook;

/**
 * Porta de ENTRADA (driving port) do domínio de Catálogo.
 * Define o contrato que o mundo externo usa para interagir com o núcleo de negócio.
 */
public interface SearchComicByIsbnUseCase {

    ComicBook execute(String isbn);
}
