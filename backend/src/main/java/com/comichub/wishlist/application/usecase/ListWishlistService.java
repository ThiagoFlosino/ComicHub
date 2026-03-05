package com.comichub.wishlist.application.usecase;

import com.comichub.wishlist.domain.model.Wishlist;
import com.comichub.wishlist.domain.port.in.ListWishlistUseCase;
import com.comichub.wishlist.domain.port.out.WishlistRepository;

import java.util.List;
import java.util.UUID;

public class ListWishlistService implements ListWishlistUseCase {

    private final WishlistRepository wishlistRepository;

    public ListWishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public List<Wishlist> list(UUID userId) {
        return wishlistRepository.findByUserId(userId);
    }
}
