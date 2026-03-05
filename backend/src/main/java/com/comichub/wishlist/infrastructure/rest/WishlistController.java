package com.comichub.wishlist.infrastructure.rest;

import com.comichub.wishlist.domain.port.in.AddToWishlistUseCase;
import com.comichub.wishlist.domain.port.in.ListWishlistUseCase;
import com.comichub.wishlist.domain.port.in.RemoveFromWishlistUseCase;
import com.comichub.wishlist.infrastructure.rest.dto.AddToWishlistRequest;
import com.comichub.wishlist.infrastructure.rest.dto.WishlistResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wishlists")
public class WishlistController {

    private final AddToWishlistUseCase addToWishlistUseCase;
    private final ListWishlistUseCase listWishlistUseCase;
    private final RemoveFromWishlistUseCase removeFromWishlistUseCase;

    public WishlistController(AddToWishlistUseCase addToWishlistUseCase,
                              ListWishlistUseCase listWishlistUseCase,
                              RemoveFromWishlistUseCase removeFromWishlistUseCase) {
        this.addToWishlistUseCase = addToWishlistUseCase;
        this.listWishlistUseCase = listWishlistUseCase;
        this.removeFromWishlistUseCase = removeFromWishlistUseCase;
    }

    @PostMapping
    public ResponseEntity<WishlistResponse> addToWishlist(
            @RequestBody @Valid AddToWishlistRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        var userId = UUID.fromString(jwt.getSubject());
        var wishlist = addToWishlistUseCase.add(userId, request.itemId(), request.targetPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(WishlistResponse.from(wishlist));
    }

    @GetMapping
    public ResponseEntity<List<WishlistResponse>> listWishlist(
            @AuthenticationPrincipal Jwt jwt) {

        var userId = UUID.fromString(jwt.getSubject());
        var wishlists = listWishlistUseCase.list(userId);
        return ResponseEntity.ok(wishlists.stream().map(WishlistResponse::from).toList());
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable UUID itemId,
            @AuthenticationPrincipal Jwt jwt) {

        var userId = UUID.fromString(jwt.getSubject());
        removeFromWishlistUseCase.remove(userId, itemId);
        return ResponseEntity.noContent().build();
    }
}
