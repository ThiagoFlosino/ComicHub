package com.comichub.catalog.application.usecase;

import com.comichub.catalog.domain.exception.ComicBookNotFoundException;
import com.comichub.catalog.domain.model.ComicBook;
import com.comichub.catalog.domain.model.Item;
import com.comichub.catalog.domain.port.in.SearchComicByIsbnUseCase;
import com.comichub.catalog.domain.port.out.CoverDownloadPort;
import com.comichub.catalog.domain.port.out.FetchBookMetadataPort;
import com.comichub.catalog.domain.port.out.ImageStoragePort;
import com.comichub.catalog.domain.port.out.ItemRepository;

/**
 * Implementação do Use Case de busca por ISBN.
 * REGRA: Nenhum import de Spring nesta camada.
 *
 * Fluxo:
 *   1. Busca metadados via FetchBookMetadataPort.
 *   2. Se houver URL de capa, faz download via CoverDownloadPort.
 *   3. Armazena os bytes no S3 via ImageStoragePort e substitui a URL pela chave S3.
 *   4. Se o download falhar, retorna a URL externa original (graceful degradation).
 *   5. Persiste (find-or-create) o ComicBook via ItemRepository e retorna o Item.
 */
public class SearchComicByIsbnService implements SearchComicByIsbnUseCase {

    private final FetchBookMetadataPort fetchBookMetadataPort;
    private final ImageStoragePort imageStoragePort;
    private final CoverDownloadPort coverDownloadPort;
    private final ItemRepository itemRepository;

    public SearchComicByIsbnService(FetchBookMetadataPort fetchBookMetadataPort,
                                    ImageStoragePort imageStoragePort,
                                    CoverDownloadPort coverDownloadPort,
                                    ItemRepository itemRepository) {
        this.fetchBookMetadataPort = fetchBookMetadataPort;
        this.imageStoragePort = imageStoragePort;
        this.coverDownloadPort = coverDownloadPort;
        this.itemRepository = itemRepository;
    }

    @Override
    public Item execute(String isbn) {
        var comicBook = fetchBookMetadataPort.fetchByIsbn(isbn)
                .orElseThrow(() -> new ComicBookNotFoundException(isbn));

        ComicBook withCover;
        if (comicBook.coverImageUrl() == null) {
            withCover = comicBook;
        } else {
            withCover = coverDownloadPort.download(comicBook.coverImageUrl())
                    .map(bytes -> {
                        var s3Key = imageStoragePort.store(isbn, bytes);
                        return new ComicBook(
                                comicBook.isbn(),
                                comicBook.title(),
                                comicBook.author(),
                                comicBook.publisher(),
                                comicBook.synopsis(),
                                s3Key
                        );
                    })
                    .orElse(comicBook);
        }

        return itemRepository.save(withCover);
    }
}
