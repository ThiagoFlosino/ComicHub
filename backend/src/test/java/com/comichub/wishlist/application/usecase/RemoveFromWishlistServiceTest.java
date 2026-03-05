package com.comichub.wishlist.application.usecase;

import com.comichub.wishlist.domain.port.out.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoveFromWishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @InjectMocks
    private RemoveFromWishlistService service;

    @Test
    void shouldCallDeleteOnRepository() {
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();

        service.remove(userId, itemId);

        verify(wishlistRepository).deleteByUserIdAndItemId(userId, itemId);
    }
}
