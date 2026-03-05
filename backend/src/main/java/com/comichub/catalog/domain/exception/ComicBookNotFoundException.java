package com.comichub.catalog.domain.exception;

/**
 * Exceção de domínio: ISBN não encontrado em nenhuma fonte externa.
 * REGRA: Nenhum import de Spring ou framework externo neste pacote.
 */
public class ComicBookNotFoundException extends RuntimeException {

    public ComicBookNotFoundException(String isbn) {
        super("Comic book not found for ISBN: " + isbn);
    }
}
