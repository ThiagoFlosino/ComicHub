package com.comichub.wishlist.infrastructure.persistence;

import com.comichub.wishlist.infrastructure.entity.WishlistEntity;
import com.comichub.wishlist.infrastructure.entity.WishlistId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataWishlistRepository extends JpaRepository<WishlistEntity, WishlistId> {
    List<WishlistEntity> findByIdUserId(UUID userId);
    void deleteByIdUserIdAndIdItemId(UUID userId, UUID itemId);
}
