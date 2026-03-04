package com.comichub.catalog.application.usecase;

import com.comichub.catalog.domain.exception.ComicBookNotFoundException;
import com.comichub.catalog.domain.model.ComicBook;
import com.comichub.catalog.domain.port.out.CoverDownloadPort;
import com.comichub.catalog.domain.port.out.FetchBookMetadataPort;
import com.comichub.catalog.domain.port.out.ImageStoragePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

    @InjectMocks
    private SearchComicByIsbnService searchComicByIsbnService;

    @Test
    void shouldReturnComicBook_whenIsbnExists() {
        // given
        var isbn = "9780930289232";
        var expected = new ComicBook(isbn, "Watchmen", "Alan Moore", "DC Comics",
                "A brilliant, multi-layered narrative.", "http://thumbnail.jpg");
        when(fetchBookMetadataPort.fetchByIsbn(isbn)).thenReturn(Optional.of(expected));
        when(coverDownloadPort.download(any())).thenReturn(Optional.empty());

        // when
        var result = searchComicByIsbnService.execute(isbn);

        // then
        assertThat(result).isEqualTo(expected);
        verify(fetchBookMetadataPort).fetchByIsbn(isbn);
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
    void shouldStoreImageAndReturnS3Key_whenCoverUrlIsPresent() {
        // given
        var isbn = "9780930289232";
        var externalUrl = "http://books.google.com/thumbnail.jpg";
        var s3Key = "covers/9780930289232.webp";
        var imageBytes = "fake-image".getBytes();

        var comic = new ComicBook(isbn, "Watchmen", "Alan Moore", "DC Comics", "Synopsis", externalUrl);
        when(fetchBookMetadataPort.fetchByIsbn(isbn)).thenReturn(Optional.of(comic));
        when(coverDownloadPort.download(externalUrl)).thenReturn(Optional.of(imageBytes));
        when(imageStoragePort.store(isbn, imageBytes)).thenReturn(s3Key);

        // when
        var result = searchComicByIsbnService.execute(isbn);

        // then
        assertThat(result.coverImageUrl()).isEqualTo(s3Key);
        verify(coverDownloadPort).download(externalUrl);
        verify(imageStoragePort).store(isbn, imageBytes);
    }

    @Test
    void shouldKeepExternalUrl_whenImageDownloadFails() {
        // given
        var isbn = "9780930289232";
        var externalUrl = "http://books.google.com/thumbnail.jpg";

        var comic = new ComicBook(isbn, "Watchmen", "Alan Moore", "DC Comics", "Synopsis", externalUrl);
        when(fetchBookMetadataPort.fetchByIsbn(isbn)).thenReturn(Optional.of(comic));
        when(coverDownloadPort.download(externalUrl)).thenReturn(Optional.empty());

        // when
        var result = searchComicByIsbnService.execute(isbn);

        // then
        assertThat(result.coverImageUrl()).isEqualTo(externalUrl);
        verify(imageStoragePort, never()).store(any(), any());
    }

    @Test
    void shouldSkipImagePipeline_whenCoverUrlIsNull() {
        // given
        var isbn = "9780930289232";
        var comic = new ComicBook(isbn, "Watchmen", "Alan Moore", "DC Comics", "Synopsis", null);
        when(fetchBookMetadataPort.fetchByIsbn(isbn)).thenReturn(Optional.of(comic));

        // when
        var result = searchComicByIsbnService.execute(isbn);

        // then
        assertThat(result.coverImageUrl()).isNull();
        verify(coverDownloadPort, never()).download(any());
        verify(imageStoragePort, never()).store(any(), any());
    }
}
