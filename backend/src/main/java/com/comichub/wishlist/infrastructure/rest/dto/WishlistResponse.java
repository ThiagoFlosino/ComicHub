package com.comichub.wishlist.infrastructure.rest.dto;

import com.comichub.wishlist.domain.model.Wishlist;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WishlistResponse(UUID userId, UUID itemId, BigDecimal targetPrice, Instant addedAt) {

    public static WishlistResponse from(Wishlist w) {
        return new WishlistResponse(w.userId(), w.itemId(), w.targetPrice(), w.addedAt());
    }
}
