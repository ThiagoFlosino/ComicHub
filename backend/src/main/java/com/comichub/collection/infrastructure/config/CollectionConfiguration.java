package com.comichub.collection.infrastructure.config;

import com.comichub.collection.application.usecase.AddToCollectionService;
import com.comichub.collection.application.usecase.ListCollectionService;
import com.comichub.collection.domain.port.in.AddToCollectionUseCase;
import com.comichub.collection.domain.port.in.ListCollectionUseCase;
import com.comichub.collection.domain.port.out.CollectionRepository;
import com.comichub.collection.infrastructure.adapter.JpaCollectionRepositoryAdapter;
import com.comichub.collection.infrastructure.persistence.SpringDataCollectionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CollectionConfiguration {

    @Bean
    public CollectionRepository collectionRepository(SpringDataCollectionRepository springDataRepo) {
        return new JpaCollectionRepositoryAdapter(springDataRepo);
    }

    @Bean
    public AddToCollectionUseCase addToCollectionUseCase(CollectionRepository collectionRepository) {
        return new AddToCollectionService(collectionRepository);
    }

    @Bean
    public ListCollectionUseCase listCollectionUseCase(CollectionRepository collectionRepository) {
        return new ListCollectionService(collectionRepository);
    }
}
