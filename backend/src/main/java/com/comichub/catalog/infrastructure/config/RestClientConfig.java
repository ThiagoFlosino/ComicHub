package com.comichub.catalog.infrastructure.config;

import com.comichub.catalog.domain.port.out.FetchBookMetadataPort;
import com.comichub.catalog.infrastructure.adapter.googlebooksapi.GoogleBooksRestAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean("googleBooksRestClient")
    public RestClient googleBooksRestClient(
            @Value("${google-books.api.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public FetchBookMetadataPort googleBooksAdapter(
            @Qualifier("googleBooksRestClient") RestClient restClient,
            @Value("${google-books.api.key:}") String apiKey) {
        return new GoogleBooksRestAdapter(restClient, apiKey);
    }
}
