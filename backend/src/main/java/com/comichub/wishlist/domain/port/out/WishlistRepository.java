package com.comichub.wishlist.domain.port.out;

import com.comichub.wishlist.domain.model.Wishlist;

import java.util.List;
import java.util.UUID;

public interface WishlistRepository {
    Wishlist save(Wishlist wishlist);
    List<Wishlist> findByUserId(UUID userId);
    void deleteByUserIdAndItemId(UUID userId, UUID itemId);
}
