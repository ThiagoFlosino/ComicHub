package com.comichub.catalog.infrastructure.adapter.http;

import com.comichub.catalog.domain.port.out.CoverDownloadPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * Adapter OUT: faz download dos bytes de uma imagem via java.net.http.HttpClient.
 */
public class HttpCoverDownloadAdapter implements CoverDownloadPort {

    private final HttpClient httpClient;

    public HttpCoverDownloadAdapter(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Optional<byte[]> download(String url) {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                return Optional.of(response.body());
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
