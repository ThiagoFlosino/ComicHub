package com.comichub.wishlist.infrastructure.rest;

import com.comichub.user.domain.port.in.ProvisionUserUseCase;
import com.comichub.user.infrastructure.security.SecurityConfig;
import com.comichub.wishlist.domain.model.Wishlist;
import com.comichub.wishlist.domain.port.in.AddToWishlistUseCase;
import com.comichub.wishlist.domain.port.in.ListWishlistUseCase;
import com.comichub.wishlist.domain.port.in.RemoveFromWishlistUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
@Import(SecurityConfig.class)
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddToWishlistUseCase addToWishlistUseCase;

    @MockitoBean
    private ListWishlistUseCase listWishlistUseCase;

    @MockitoBean
    private RemoveFromWishlistUseCase removeFromWishlistUseCase;

    @MockitoBean
    private ProvisionUserUseCase provisionUserUseCase;

    // ─── POST – Segurança ─────────────────────────────────────────────────────

    @Test
    void shouldReturn401_whenPostWithoutToken() throws Exception {
        mockMvc.perform(post("/api/v1/wishlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": \"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    // ─── POST – Endpoint ──────────────────────────────────────────────────────

    @Test
    void shouldReturn201_whenValidJwtAndBodyWithTargetPrice() throws Exception {
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();
        var targetPrice = new BigDecimal("12.99");
        var saved = new Wishlist(userId, itemId, targetPrice, Instant.now());
        when(addToWishlistUseCase.add(eq(userId), eq(itemId), any())).thenReturn(saved);

        mockMvc.perform(post("/api/v1/wishlists")
                        .with(jwt().jwt(j -> j.subject(userId.toString())
                                              .claim("email", "user@test.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": \"" + itemId + "\", \"targetPrice\": 12.99}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.itemId").value(itemId.toString()))
                .andExpect(jsonPath("$.targetPrice").value(12.99));
    }

    @Test
    void shouldReturn201_whenTargetPriceIsNull() throws Exception {
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();
        var saved = new Wishlist(userId, itemId, null, Instant.now());
        when(addToWishlistUseCase.add(any(), any(), any())).thenReturn(saved);

        mockMvc.perform(post("/api/v1/wishlists")
                        .with(jwt().jwt(j -> j.subject(userId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": \"" + itemId + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.targetPrice").doesNotExist());
    }

    @Test
    void shouldReturn400_whenItemIdIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/wishlists")
                        .with(jwt().jwt(j -> j.subject(UUID.randomUUID().toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetPrice\": 10.00}"))
                .andExpect(status().isBadRequest());
    }

    // ─── GET – Segurança ──────────────────────────────────────────────────────

    @Test
    void shouldReturn401_whenGetWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/wishlists"))
                .andExpect(status().isUnauthorized());
    }

    // ─── GET – Endpoint ───────────────────────────────────────────────────────

    @Test
    void shouldReturn200WithList_whenValidJwt() throws Exception {
        var userId = UUID.randomUUID();
        var items = List.of(
                new Wishlist(userId, UUID.randomUUID(), new BigDecimal("10.00"), Instant.now()),
                new Wishlist(userId, UUID.randomUUID(), null, Instant.now())
        );
        when(listWishlistUseCase.list(userId)).thenReturn(items);

        mockMvc.perform(get("/api/v1/wishlists")
                        .with(jwt().jwt(j -> j.subject(userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ─── DELETE – Segurança ───────────────────────────────────────────────────

    @Test
    void shouldReturn401_whenDeleteWithoutToken() throws Exception {
        mockMvc.perform(delete("/api/v1/wishlists/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    // ─── DELETE – Endpoint ────────────────────────────────────────────────────

    @Test
    void shouldReturn204_whenValidJwt() throws Exception {
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/wishlists/" + itemId)
                        .with(jwt().jwt(j -> j.subject(userId.toString()))))
                .andExpect(status().isNoContent());

        verify(removeFromWishlistUseCase).remove(userId, itemId);
    }
}
