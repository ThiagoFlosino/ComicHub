package com.comichub.wishlist.application.usecase;

import com.comichub.wishlist.domain.model.Wishlist;
import com.comichub.wishlist.domain.port.in.AddToWishlistUseCase;
import com.comichub.wishlist.domain.port.out.WishlistRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AddToWishlistService implements AddToWishlistUseCase {

    private final WishlistRepository wishlistRepository;

    public AddToWishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public Wishlist add(UUID userId, UUID itemId, BigDecimal targetPrice) {
        var wishlist = new Wishlist(userId, itemId, targetPrice, Instant.now());
        return wishlistRepository.save(wishlist);
    }
}
