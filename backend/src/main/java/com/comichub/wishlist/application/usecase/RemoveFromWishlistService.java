package com.comichub.wishlist.application.usecase;

import com.comichub.wishlist.domain.port.in.RemoveFromWishlistUseCase;
import com.comichub.wishlist.domain.port.out.WishlistRepository;

import java.util.UUID;

public class RemoveFromWishlistService implements RemoveFromWishlistUseCase {

    private final WishlistRepository wishlistRepository;

    public RemoveFromWishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public void remove(UUID userId, UUID itemId) {
        wishlistRepository.deleteByUserIdAndItemId(userId, itemId);
    }
}
