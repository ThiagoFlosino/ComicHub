package com.comichub.wishlist.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Wishlist(UUID userId, UUID itemId, BigDecimal targetPrice, Instant addedAt) {}
