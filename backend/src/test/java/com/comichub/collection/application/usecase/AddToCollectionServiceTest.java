package com.comichub.collection.application.usecase;

import com.comichub.collection.domain.model.Collection;
import com.comichub.collection.domain.model.CollectionStatus;
import com.comichub.collection.domain.port.out.CollectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddToCollectionServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @InjectMocks
    private AddToCollectionService service;

    @Test
    void shouldSaveAndReturnCollection_withStatus() {
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();
        var saved = new Collection(userId, itemId, "Estante 1", CollectionStatus.OWNED, Instant.now());
        when(collectionRepository.save(any())).thenReturn(saved);

        var result = service.add(userId, itemId, "Estante 1", CollectionStatus.OWNED);

        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.itemId()).isEqualTo(itemId);
        assertThat(result.status()).isEqualTo(CollectionStatus.OWNED);
        verify(collectionRepository).save(any(Collection.class));
    }

    @Test
    void shouldSetAddedAtTimestamp_whenAddingItem() {
        var before = Instant.now();
        when(collectionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = service.add(UUID.randomUUID(), UUID.randomUUID(), null, CollectionStatus.READING);

        assertThat(result.addedAt()).isAfterOrEqualTo(before);
        assertThat(result.status()).isEqualTo(CollectionStatus.READING);
    }

    @Test
    void shouldPersistLentStatus_whenItemIsLentToFriend() {
        when(collectionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = service.add(UUID.randomUUID(), UUID.randomUUID(), "Amigo João", CollectionStatus.LENT);

        assertThat(result.status()).isEqualTo(CollectionStatus.LENT);
        assertThat(result.shelfLocation()).isEqualTo("Amigo João");
    }
}
