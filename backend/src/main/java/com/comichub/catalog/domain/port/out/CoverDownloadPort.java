package com.comichub.catalog.domain.port.out;

import java.util.Optional;

/**
 * Porta de SAÍDA: faz download dos bytes de uma imagem a partir de uma URL externa.
 * REGRA: Nenhum import de Spring ou infraestrutura neste pacote.
 */
public interface CoverDownloadPort {

    /**
     * Baixa os bytes brutos de uma imagem a partir de uma URL.
     *
     * @param url URL pública da imagem
     * @return bytes da imagem, ou Optional.empty() se o download falhar
     */
    Optional<byte[]> download(String url);
}
