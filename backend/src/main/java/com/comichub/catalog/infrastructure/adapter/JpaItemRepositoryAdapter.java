package com.comichub.catalog.infrastructure.adapter;

import com.comichub.catalog.domain.model.ComicBook;
import com.comichub.catalog.domain.model.Item;
import com.comichub.catalog.domain.port.out.ItemRepository;
import com.comichub.catalog.infrastructure.entity.ItemEntity;
import com.comichub.catalog.infrastructure.persistence.SpringDataItemRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JpaItemRepositoryAdapter implements ItemRepository {

    private final SpringDataItemRepository springDataItemRepository;

    public JpaItemRepositoryAdapter(SpringDataItemRepository springDataItemRepository) {
        this.springDataItemRepository = springDataItemRepository;
    }

    @Override
    public Item save(ComicBook comicBook) {
        return springDataItemRepository.findByIsbn(comicBook.isbn())
                .map(existing -> {
                    existing.setCoverImage(comicBook.coverImageUrl());
                    return toDomain(springDataItemRepository.save(existing));
                })
                .orElseGet(() -> {
                    var entity = new ItemEntity(
                            comicBook.isbn(),
                            comicBook.title(),
                            comicBook.author(),
                            comicBook.publisher(),
                            comicBook.synopsis(),
                            null,
                            null,
                            null,
                            comicBook.coverImageUrl(),
                            Instant.now()
                    );
                    return toDomain(springDataItemRepository.save(entity));
                });
    }

    @Override
    public Optional<Item> findById(UUID id) {
        return springDataItemRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Item> findByIsbn(String isbn) {
        return springDataItemRepository.findByIsbn(isbn).map(this::toDomain);
    }

    @Override
    public List<Item> findBySeries(String series) {
        return springDataItemRepository.findBySeries(series).stream()
                .map(this::toDomain)
                .toList();
    }

    private Item toDomain(ItemEntity e) {
        return new Item(
                e.getId(),
                e.getIsbn(),
                e.getTitle(),
                e.getAuthor(),
                e.getPublisher(),
                e.getSynopsis(),
                e.getSeries(),
                e.getVolume(),
                e.getVariant(),
                e.getCoverImage(),
                e.getCreatedAt()
        );
    }
}
