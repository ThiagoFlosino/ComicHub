package com.comichub.catalog.infrastructure.adapter.googlebooksapi;

import com.comichub.catalog.domain.model.ComicBook;
import com.comichub.catalog.domain.port.out.FetchBookMetadataPort;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

/**
 * Adapter OUT: chama a Google Books API e converte o resultado em ComicBook.
 * Instanciado via RestClientConfig (infrastructure/config) — sem @Component aqui.
 *
 * [TDD – RED] Implementação ainda não fornecida.
 */
public class GoogleBooksRestAdapter implements FetchBookMetadataPort {

    private final RestClient restClient;
    private final String apiKey;

    public GoogleBooksRestAdapter(RestClient restClient, String apiKey) {
        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    @Override
    public Optional<ComicBook> fetchByIsbn(String isbn) {
        var response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", "isbn:" + isbn)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .body(GoogleBooksResponse.class);

        if (response == null || response.totalItems() == 0
                || response.items() == null || response.items().isEmpty()) {
            return Optional.empty();
        }

        var info = response.items().get(0).volumeInfo();
        if (info == null) {
            return Optional.empty();
        }

        String author = (info.authors() == null || info.authors().isEmpty())
                ? null
                : String.join(", ", info.authors());

        String coverImageUrl = (info.imageLinks() != null)
                ? info.imageLinks().thumbnail()
                : null;

        String resolvedIsbn = resolveIsbn(info.industryIdentifiers(), isbn);

        return Optional.of(new ComicBook(
                resolvedIsbn,
                info.title(),
                author,
                info.publisher(),
                info.description(),
                coverImageUrl
        ));
    }

    private String resolveIsbn(List<GoogleBooksResponse.IndustryIdentifier> identifiers, String fallback) {
        if (identifiers == null) return fallback;
        return identifiers.stream()
                .filter(id -> "ISBN_13".equals(id.type()))
                .map(GoogleBooksResponse.IndustryIdentifier::identifier)
                .findFirst()
                .orElse(fallback);
    }
}
