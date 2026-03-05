package com.comichub.wishlist.infrastructure.adapter;

import com.comichub.wishlist.domain.model.Wishlist;
import com.comichub.wishlist.domain.port.out.WishlistRepository;
import com.comichub.wishlist.infrastructure.entity.WishlistEntity;
import com.comichub.wishlist.infrastructure.entity.WishlistId;
import com.comichub.wishlist.infrastructure.persistence.SpringDataWishlistRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public class JpaWishlistRepositoryAdapter implements WishlistRepository {

    private final SpringDataWishlistRepository springDataWishlistRepository;

    public JpaWishlistRepositoryAdapter(SpringDataWishlistRepository springDataWishlistRepository) {
        this.springDataWishlistRepository = springDataWishlistRepository;
    }

    @Override
    public Wishlist save(Wishlist wishlist) {
        var id = new WishlistId(wishlist.userId(), wishlist.itemId());
        var entity = new WishlistEntity(id, wishlist.targetPrice(), wishlist.addedAt());
        return toDomain(springDataWishlistRepository.save(entity));
    }

    @Override
    public List<Wishlist> findByUserId(UUID userId) {
        return springDataWishlistRepository.findByIdUserId(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteByUserIdAndItemId(UUID userId, UUID itemId) {
        springDataWishlistRepository.deleteByIdUserIdAndIdItemId(userId, itemId);
    }

    private Wishlist toDomain(WishlistEntity e) {
        return new Wishlist(
                e.getId().getUserId(),
                e.getId().getItemId(),
                e.getTargetPrice(),
                e.getAddedAt()
        );
    }
}
