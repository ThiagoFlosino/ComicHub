package com.comichub.collection.application.usecase;

import com.comichub.collection.domain.model.Collection;
import com.comichub.collection.domain.model.CollectionFilter;
import com.comichub.collection.domain.model.CollectionStatus;
import com.comichub.collection.domain.port.out.CollectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListCollectionServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @InjectMocks
    private ListCollectionService service;

    @Test
    void shouldReturnAllCollections_whenFilterIsEmpty() {
        var userId = UUID.randomUUID();
        var filter = CollectionFilter.empty();
        var items = List.of(
                new Collection(userId, UUID.randomUUID(), "Shelf 1", CollectionStatus.OWNED, Instant.now()),
                new Collection(userId, UUID.randomUUID(), "Shelf 2", CollectionStatus.READING, Instant.now())
        );
        when(collectionRepository.findByUserIdAndFilter(eq(userId), eq(filter))).thenReturn(items);

        var result = service.list(userId, filter);

        assertThat(result).hasSize(2);
        verify(collectionRepository).findByUserIdAndFilter(userId, filter);
    }

    @Test
    void shouldDelegateFilterToRepository() {
        var userId = UUID.randomUUID();
        var filter = new CollectionFilter(CollectionStatus.READING, "Watchmen");
        when(collectionRepository.findByUserIdAndFilter(userId, filter)).thenReturn(List.of());

        service.list(userId, filter);

        verify(collectionRepository).findByUserIdAndFilter(userId, filter);
    }

    @Test
    void shouldReturnEmptyList_whenUserHasNoItems() {
        var userId = UUID.randomUUID();
        when(collectionRepository.findByUserIdAndFilter(eq(userId), any())).thenReturn(List.of());

        var result = service.list(userId, CollectionFilter.empty());

        assertThat(result).isEmpty();
    }
}
