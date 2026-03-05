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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddToWishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @InjectMocks
    private AddToWishlistService service;

    @Test
    void shouldSaveAndReturnWishlist_withTargetPrice() {
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();
        var targetPrice = new BigDecimal("12.99");
        var saved = new Wishlist(userId, itemId, targetPrice, Instant.now());
        when(wishlistRepository.save(any())).thenReturn(saved);

        var result = service.add(userId, itemId, targetPrice);

        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.itemId()).isEqualTo(itemId);
        assertThat(result.targetPrice()).isEqualByComparingTo(targetPrice);
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void shouldSaveWishlist_whenTargetPriceIsNull() {
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();
        var saved = new Wishlist(userId, itemId, null, Instant.now());
        when(wishlistRepository.save(any())).thenReturn(saved);

        var result = service.add(userId, itemId, null);

        assertThat(result.targetPrice()).isNull();
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void shouldSetAddedAtTimestamp_whenAdding() {
        var before = Instant.now();
        when(wishlistRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = service.add(UUID.randomUUID(), UUID.randomUUID(), null);

        assertThat(result.addedAt()).isAfterOrEqualTo(before);
        verify(wishlistRepository).save(any(Wishlist.class));
    }
}
