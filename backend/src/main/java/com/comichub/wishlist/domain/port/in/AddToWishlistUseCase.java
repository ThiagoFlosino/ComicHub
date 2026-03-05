package com.comichub.wishlist.domain.port.in;

import com.comichub.wishlist.domain.model.Wishlist;

import java.math.BigDecimal;
import java.util.UUID;

public interface AddToWishlistUseCase {
    Wishlist add(UUID userId, UUID itemId, BigDecimal targetPrice);
}
