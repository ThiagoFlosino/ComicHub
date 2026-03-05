package com.comichub.wishlist.application.usecase;

import com.comichub.wishlist.domain.model.Wishlist;
import com.comichub.wishlist.domain.port.out.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListWishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @InjectMocks
    private ListWishlistService service;

    @Test
    void shouldReturnEmptyList_whenNoItemsInWishlist() {
        var userId = UUID.randomUUID();
        when(wishlistRepository.findByUserId(userId)).thenReturn(List.of());

        var result = service.list(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnAllItemsForUser() {
        var userId = UUID.randomUUID();
        var items = List.of(
                new Wishlist(userId, UUID.randomUUID(), new BigDecimal("10.00"), Instant.now()),
                new Wishlist(userId, UUID.randomUUID(), null, Instant.now())
        );
        when(wishlistRepository.findByUserId(userId)).thenReturn(items);

        var result = service.list(userId);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(items);
    }
}
