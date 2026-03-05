package com.comichub.catalog.domain.model;

/**
 * Entidade rica do domínio de Catálogo.
 * Representa os metadados de um quadrinho/mangá retornados por fontes externas.
 * REGRA: Nenhum import de Spring, JPA ou qualquer framework externo neste pacote.
 */
public record ComicBook(
        String isbn,
        String title,
        String author,
        String publisher,
        String synopsis,
        String coverImageUrl
) {}
