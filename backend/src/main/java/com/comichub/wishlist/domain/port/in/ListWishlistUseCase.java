package com.comichub.wishlist.domain.port.in;

import com.comichub.wishlist.domain.model.Wishlist;

import java.util.List;
import java.util.UUID;

public interface ListWishlistUseCase {
    List<Wishlist> list(UUID userId);
}
