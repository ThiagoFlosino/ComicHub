package com.comichub.collection.domain.model;

/**
 * Parâmetros de filtro para listagem do acervo.
 * Campos nulos significam "sem filtro".
 */
public record CollectionFilter(CollectionStatus status, String series) {

    public static CollectionFilter empty() {
        return new CollectionFilter(null, null);
    }
}
