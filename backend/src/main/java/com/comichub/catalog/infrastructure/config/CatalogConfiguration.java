package com.comichub.catalog.infrastructure.config;

import com.comichub.catalog.application.usecase.SearchComicByIsbnService;
import com.comichub.catalog.domain.port.in.SearchComicByIsbnUseCase;
import com.comichub.catalog.domain.port.out.CoverDownloadPort;
import com.comichub.catalog.domain.port.out.FetchBookMetadataPort;
import com.comichub.catalog.domain.port.out.ImageStoragePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wiring do módulo Catalog.
 * Registra os Use Cases como beans Spring sem poluir as camadas domain/application
 * com anotações @Service ou @Component.
 */
@Configuration
public class CatalogConfiguration {

    @Bean
    public SearchComicByIsbnUseCase searchComicByIsbnUseCase(
            FetchBookMetadataPort fetchBookMetadataPort,
            ImageStoragePort imageStoragePort,
            CoverDownloadPort coverDownloadPort) {
        return new SearchComicByIsbnService(fetchBookMetadataPort, imageStoragePort, coverDownloadPort);
    }
}
