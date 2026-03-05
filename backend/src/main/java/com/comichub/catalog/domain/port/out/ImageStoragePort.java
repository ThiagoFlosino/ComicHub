package com.comichub.catalog.domain.port.out;

/**
 * Porta de SAÍDA: armazena bytes de imagem e retorna o caminho no storage.
 * REGRA: Nenhum import de Spring ou infraestrutura neste pacote.
 */
public interface ImageStoragePort {

    /**
     * Armazena os bytes de imagem associados ao ISBN e retorna a chave gerada no storage.
     *
     * @param isbn       ISBN-13 do item (usado para compor o caminho)
     * @param imageBytes bytes da imagem (já convertida ou raw)
     * @return caminho/chave no storage (ex: "covers/9780930289232.webp")
     */
    String store(String isbn, byte[] imageBytes);
}
