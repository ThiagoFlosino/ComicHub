package com.comichub.wishlist.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class WishlistId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "item_id")
    private UUID itemId;

    protected WishlistId() {}

    public WishlistId(UUID userId, UUID itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }

    public UUID getUserId() { return userId; }
    public UUID getItemId() { return itemId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WishlistId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, itemId);
    }
}
