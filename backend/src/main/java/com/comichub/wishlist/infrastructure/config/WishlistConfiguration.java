package com.comichub.wishlist.infrastructure.config;

import com.comichub.wishlist.application.usecase.AddToWishlistService;
import com.comichub.wishlist.application.usecase.ListWishlistService;
import com.comichub.wishlist.application.usecase.RemoveFromWishlistService;
import com.comichub.wishlist.domain.port.in.AddToWishlistUseCase;
import com.comichub.wishlist.domain.port.in.ListWishlistUseCase;
import com.comichub.wishlist.domain.port.in.RemoveFromWishlistUseCase;
import com.comichub.wishlist.domain.port.out.WishlistRepository;
import com.comichub.wishlist.infrastructure.adapter.JpaWishlistRepositoryAdapter;
import com.comichub.wishlist.infrastructure.persistence.SpringDataWishlistRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WishlistConfiguration {

    @Bean
    public WishlistRepository wishlistRepository(SpringDataWishlistRepository springDataRepo) {
        return new JpaWishlistRepositoryAdapter(springDataRepo);
    }

    @Bean
    public AddToWishlistUseCase addToWishlistUseCase(WishlistRepository wishlistRepository) {
        return new AddToWishlistService(wishlistRepository);
    }

    @Bean
    public ListWishlistUseCase listWishlistUseCase(WishlistRepository wishlistRepository) {
        return new ListWishlistService(wishlistRepository);
    }

    @Bean
    public RemoveFromWishlistUseCase removeFromWishlistUseCase(WishlistRepository wishlistRepository) {
        return new RemoveFromWishlistService(wishlistRepository);
    }
}
