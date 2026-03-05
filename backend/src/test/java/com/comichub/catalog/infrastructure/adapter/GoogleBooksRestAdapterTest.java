package com.comichub.catalog.infrastructure.adapter;

import com.comichub.catalog.infrastructure.adapter.googlebooksapi.GoogleBooksRestAdapter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Task #002 – Teste de integração do adapter (MockWebServer, sem Spring context).
 *
 * Valida que GoogleBooksRestAdapter:
 *   1. Converte a resposta JSON da API em ComicBook corretamente.
 *   2. Retorna Optional.empty() quando o ISBN não é encontrado.
 *   3. Envia a query correta para a API (isbn:{isbn}).
 */
class GoogleBooksRestAdapterTest {

    private MockWebServer mockWebServer;
    private GoogleBooksRestAdapter adapter;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        var restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        adapter = new GoogleBooksRestAdapter(restClient, "test-api-key");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnComicBook_whenIsbnFound() throws InterruptedException {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                          "totalItems": 1,
                          "items": [{
                            "volumeInfo": {
                              "title": "Watchmen",
                              "authors": ["Alan Moore", "Dave Gibbons"],
                              "publisher": "DC Comics",
                              "description": "A brilliant, multi-layered narrative.",
                              "industryIdentifiers": [
                                {"type": "ISBN_13", "identifier": "9780930289232"}
                              ],
                              "imageLinks": {
                                "thumbnail": "http://books.google.com/thumbnail.jpg"
                              }
                            }
                          }]
                        }
                        """)
                .addHeader("Content-Type", "application/json"));

        // when
        var result = adapter.fetchByIsbn("9780930289232");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().isbn()).isEqualTo("9780930289232");
        assertThat(result.get().title()).isEqualTo("Watchmen");
        assertThat(result.get().author()).isEqualTo("Alan Moore, Dave Gibbons");
        assertThat(result.get().publisher()).isEqualTo("DC Comics");
        assertThat(result.get().synopsis()).isEqualTo("A brilliant, multi-layered narrative.");
        assertThat(result.get().coverImageUrl()).isEqualTo("http://books.google.com/thumbnail.jpg");

        var request = mockWebServer.takeRequest();
        assertThat(request.getPath()).contains("isbn:9780930289232");
    }

    @Test
    void shouldReturnEmpty_whenTotalItemsIsZero() {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        { "totalItems": 0 }
                        """)
                .addHeader("Content-Type", "application/json"));

        // when
        var result = adapter.fetchByIsbn("0000000000000");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmpty_whenItemsListIsEmpty() {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        { "totalItems": 0, "items": [] }
                        """)
                .addHeader("Content-Type", "application/json"));

        // when
        var result = adapter.fetchByIsbn("0000000000000");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleMissingOptionalFields() throws InterruptedException {
        // given – sem authors, sem imageLinks
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                          "totalItems": 1,
                          "items": [{
                            "volumeInfo": {
                              "title": "Obra Anônima",
                              "publisher": "Editora Desconhecida"
                            }
                          }]
                        }
                        """)
                .addHeader("Content-Type", "application/json"));

        // when
        var result = adapter.fetchByIsbn("9780000000001");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().title()).isEqualTo("Obra Anônima");
        assertThat(result.get().author()).isNull();
        assertThat(result.get().coverImageUrl()).isNull();
    }
}
