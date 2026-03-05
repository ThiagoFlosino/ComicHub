package com.comichub.wishlist.infrastructure.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AddToWishlistRequest(@NotNull UUID itemId, BigDecimal targetPrice) {}
