package com.comichub.wishlist.domain.port.in;

import java.util.UUID;

public interface RemoveFromWishlistUseCase {
    void remove(UUID userId, UUID itemId);
}
