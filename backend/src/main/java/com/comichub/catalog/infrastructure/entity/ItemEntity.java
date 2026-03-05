package com.comichub.catalog.infrastructure.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "items")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 255)
    private String author;

    @Column(length = 255)
    private String publisher;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column(length = 255)
    private String series;

    private Integer volume;

    @Column(length = 255)
    private String variant;

    @Column(length = 500)
    private String coverImage;

    @Column(nullable = false)
    private Instant createdAt;

    protected ItemEntity() {}

    public ItemEntity(String isbn, String title, String author, String publisher,
                      String synopsis, String series, Integer volume, String variant,
                      String coverImage, Instant createdAt) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.synopsis = synopsis;
        this.series = series;
        this.volume = volume;
        this.variant = variant;
        this.coverImage = coverImage;
        this.createdAt = createdAt;
    }

    public UUID getId()          { return id; }
    public String getIsbn()      { return isbn; }
    public String getTitle()     { return title; }
    public String getAuthor()    { return author; }
    public String getPublisher() { return publisher; }
    public String getSynopsis()  { return synopsis; }
    public String getSeries()    { return series; }
    public Integer getVolume()   { return volume; }
    public String getVariant()   { return variant; }
    public String getCoverImage(){ return coverImage; }
    public Instant getCreatedAt(){ return createdAt; }

    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
}
