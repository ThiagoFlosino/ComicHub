package com.comichub.catalog.application.usecase;

import com.comichub.catalog.domain.exception.ComicBookNotFoundException;
import com.comichub.catalog.domain.model.ComicBook;
import com.comichub.catalog.domain.model.Item;
import com.comichub.catalog.domain.port.out.CoverDownloadPort;
import com.comichub.catalog.domain.port.out.FetchBookMetadataPort;
import com.comichub.catalog.domain.port.out.ImageStoragePort;
import com.comichub.catalog.domain.port.out.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Task #002/#003 – Teste unitário do Use Case (Mockito, sem Spring context).
 */
@ExtendWith(MockitoExtension.class)
class SearchComicByIsbnServiceTest {

    @Mock
    private FetchBookMetadataPort fetchBookMetadataPort;

    @Mock
    private ImageStoragePort imageStoragePort;

    @Mock
    private CoverDownloadPort coverDownloadPort;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private SearchComicByIsbnService searchComicByIsbnService;

    private Item buildItem(String isbn, String title, String coverImage) {
        return new Item(UUID.randomUUID(), isbn, title, "Alan Moore", "DC Comics",
                "A brilliant, multi-layered narrative.", null, null, null, coverImage, Instant.now());
    }

    @Test
    void shouldReturnItem_whenIsbnExists() {
        // given
        var isbn = "9780930289232";
        var comic = new ComicBook(isbn, "Watchmen", "Alan Moore", "DC Comics",
                "A brilliant, multi-layered narrative.", "http://thumbnail.jpg");
        var savedItem = buildItem(isbn, "Watchmen", "http://thumbnail.jpg");

        when(fetchBookMetadataPort.fetchByIsbn(isbn)).thenReturn(Optional.of(comic));
        when(coverDownloadPort.download(any())).thenReturn(Optional.empty());
        when(itemRepository.save(any(ComicBook.class))).thenReturn(savedItem);

        // when
        var result = searchComicByIsbnService.execute(isbn);

        // then
        assertThat(result).isInstanceOf(Item.class);
        assertThat(result.isbn()).isEqualTo(isbn);
        verify(fetchBookMetadataPort).fetchByIsbn(isbn);
        verify(itemRepository).save(any(ComicBook.class));
    }

    @Test
    void shouldThrowComicBookNotFoundException_whenIsbnNotFound() {
        // given
        var isbn = "0000000000000";
        when(fetchBookMetadataPort.fetchByIsbn(isbn)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> searchComicByIsbnService.execute(isbn))
                .isInstanceOf(ComicBookNotFoundException.class)
                .hasMessageContaining(isbn);
    }

    @Test
    void shouldStoreImageAndReturnItemWithS3Key_whenCoverUrlIsPresent() {
        // given
        var isbn = "9780930289232";
        var externalUrl = "http://books.google.com/thumbnail.jpg";
        var s3Key = "covers/9780930289232.webp";
        var imageBytes = "fake-image".getBytes();

        var comic = new ComicBook(isbn, "Watchmen", "Alan Moore", "DC Comics", "Synopsis", externalUrl);
        var savedItem = buildItem(isbn, "Watchmen", s3Key);

        when(fetchBookMetadataPort.fetchByIsbn(isbn)).thenReturn(Optional.of(comic));
        when(coverDownloadPort.download(externalUrl)).thenReturn(Optional.of(imageBytes));
        when(imageStoragePort.store(isbn, imageBytes)).thenReturn(s3Key);
        when(itemRepository.save(any(ComicBook.class))).thenReturn(savedItem);

        // when
        var result = searchComicByIsbnService.execute(isbn);

        // then
        assertThat(result.coverImage()).isEqualTo(s3Key);
        verify(coverDownloadPort).download(externalUrl);
        verify(imageStoragePort).store(isbn, imageBytes);
        verify(itemRepository).save(any(ComicBook.class));
    }

    @Test
    void shouldKeepExternalUrl_whenImageDownloadFails() {
        // given
        var isbn = "9780930289232";
        var externalUrl = "http://books.google.com/thumbnail.jpg";

        var comic = new ComicBook(isbn, "Watchmen", "Alan Moore", "DC Comics", "Synopsis", externalUrl);
        var savedItem = buildItem(isbn, "Watchmen", externalUrl);

        when(fetchBookMetadataPort.fetchByIsbn(isbn)).thenReturn(Optional.of(comic));
        when(coverDownloadPort.download(externalUrl)).thenReturn(Optional.empty());
        when(itemRepository.save(any(ComicBook.class))).thenReturn(savedItem);

        // when
        var result = searchComicByIsbnService.execute(isbn);

        // then
        assertThat(result.coverImage()).isEqualTo(externalUrl);
        verify(imageStoragePort, never()).store(any(), any());
        verify(itemRepository).save(any(ComicBook.class));
    }

    @Test
    void shouldSkipImagePipeline_whenCoverUrlIsNull() {
        // given
        var isbn = "9780930289232";
        var comic = new ComicBook(isbn, "Watchmen", "Alan Moore", "DC Comics", "Synopsis", null);
        var savedItem = buildItem(isbn, "Watchmen", null);

        when(fetchBookMetadataPort.fetchByIsbn(isbn)).thenReturn(Optional.of(comic));
        when(itemRepository.save(any(ComicBook.class))).thenReturn(savedItem);

        // when
        var result = searchComicByIsbnService.execute(isbn);

        // then
        assertThat(result.coverImage()).isNull();
        verify(coverDownloadPort, never()).download(any());
        verify(imageStoragePort, never()).store(any(), any());
        verify(itemRepository).save(any(ComicBook.class));
    }

    @Test
    void shouldReturnExistingItem_whenIsbnAlreadyPersisted() {
        // given
        var isbn = "9780930289232";
        var comic = new ComicBook(isbn, "Watchmen", "Alan Moore", "DC Comics", "Synopsis", null);
        var existingItem = buildItem(isbn, "Watchmen", "covers/existing.webp");

        when(fetchBookMetadataPort.fetchByIsbn(isbn)).thenReturn(Optional.of(comic));
        when(itemRepository.save(any(ComicBook.class))).thenReturn(existingItem);

        // when
        var result = searchComicByIsbnService.execute(isbn);

        // then
        assertThat(result.id()).isEqualTo(existingItem.id());
        verify(itemRepository).save(any(ComicBook.class));
    }
}
