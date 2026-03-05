package com.comichub.wishlist.infrastructure.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "wishlists")
public class WishlistEntity {

    @EmbeddedId
    private WishlistId id;

    private BigDecimal targetPrice;

    private Instant addedAt;

    protected WishlistEntity() {}

    public WishlistEntity(WishlistId id, BigDecimal targetPrice, Instant addedAt) {
        this.id = id;
        this.targetPrice = targetPrice;
        this.addedAt = addedAt;
    }

    public WishlistId getId()          { return id; }
    public BigDecimal getTargetPrice() { return targetPrice; }
    public Instant getAddedAt()        { return addedAt; }
}
