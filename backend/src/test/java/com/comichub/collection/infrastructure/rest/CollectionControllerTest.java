package com.comichub.collection.infrastructure.rest;

import com.comichub.collection.domain.model.Collection;
import com.comichub.collection.domain.model.CollectionStatus;
import com.comichub.collection.domain.port.in.AddToCollectionUseCase;
import com.comichub.collection.domain.port.in.ListCollectionUseCase;
import com.comichub.user.domain.port.in.ProvisionUserUseCase;
import com.comichub.user.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CollectionController.class)
@Import(SecurityConfig.class)
class CollectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddToCollectionUseCase addToCollectionUseCase;

    @MockitoBean
    private ListCollectionUseCase listCollectionUseCase;

    // Necessário para o SecurityConfig criar o CognitoJwtConverter sem DB
    @MockitoBean
    private ProvisionUserUseCase provisionUserUseCase;

    // ─── POST – Segurança ─────────────────────────────────────────────────────

    @Test
    void shouldReturn401_whenPostWithoutToken() throws Exception {
        mockMvc.perform(post("/api/v1/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": \"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    // ─── POST – Endpoint ──────────────────────────────────────────────────────

    @Test
    void shouldReturn201_whenValidJwtAndBody() throws Exception {
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();
        var saved = new Collection(userId, itemId, "Estante 1", CollectionStatus.OWNED, Instant.now());
        when(addToCollectionUseCase.add(eq(userId), eq(itemId), any(), any())).thenReturn(saved);

        mockMvc.perform(post("/api/v1/collections")
                        .with(jwt().jwt(j -> j.subject(userId.toString())
                                              .claim("email", "user@test.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": \"" + itemId + "\", \"shelfLocation\": \"Estante 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.itemId").value(itemId.toString()))
                .andExpect(jsonPath("$.status").value("OWNED"))
                .andExpect(jsonPath("$.shelfLocation").value("Estante 1"));
    }

    @Test
    void shouldReturn400_whenItemIdIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/collections")
                        .with(jwt().jwt(j -> j.subject(UUID.randomUUID().toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shelfLocation\": \"Estante 1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDefaultToOwnedStatus_whenStatusNotProvided() throws Exception {
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();
        var saved = new Collection(userId, itemId, null, CollectionStatus.OWNED, Instant.now());
        when(addToCollectionUseCase.add(any(), any(), any(), any())).thenReturn(saved);

        mockMvc.perform(post("/api/v1/collections")
                        .with(jwt().jwt(j -> j.subject(userId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": \"" + itemId + "\"}"))
                .andExpect(status().isCreated());

        verify(addToCollectionUseCase).add(eq(userId), eq(itemId), any(), eq(CollectionStatus.OWNED));
    }

    @Test
    void shouldPassStatusToUseCase_whenStatusProvided() throws Exception {
        var userId = UUID.randomUUID();
        var itemId = UUID.randomUUID();
        var saved = new Collection(userId, itemId, null, CollectionStatus.READING, Instant.now());
        when(addToCollectionUseCase.add(any(), any(), any(), eq(CollectionStatus.READING))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/collections")
                        .with(jwt().jwt(j -> j.subject(userId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": \"" + itemId + "\", \"status\": \"READING\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("READING"));

        verify(addToCollectionUseCase).add(eq(userId), eq(itemId), any(), eq(CollectionStatus.READING));
    }

    // ─── GET – Segurança ──────────────────────────────────────────────────────

    @Test
    void shouldReturn401_whenGetWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/collections"))
                .andExpect(status().isUnauthorized());
    }

    // ─── GET – Endpoint ───────────────────────────────────────────────────────

    @Test
    void shouldReturn200AndEmptyList_whenNoCollections() throws Exception {
        var userId = UUID.randomUUID();
        when(listCollectionUseCase.list(eq(userId), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/collections")
                        .with(jwt().jwt(j -> j.subject(userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturn200WithItems_whenCollectionsExist() throws Exception {
        var userId = UUID.randomUUID();
        var items = List.of(
                new Collection(userId, UUID.randomUUID(), "Estante 1", CollectionStatus.OWNED, Instant.now()),
                new Collection(userId, UUID.randomUUID(), null, CollectionStatus.READING, Instant.now())
        );
        when(listCollectionUseCase.list(eq(userId), any())).thenReturn(items);

        mockMvc.perform(get("/api/v1/collections")
                        .with(jwt().jwt(j -> j.subject(userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("OWNED"))
                .andExpect(jsonPath("$[1].status").value("READING"));
    }

    @Test
    void shouldPassStatusFilterToUseCase_whenStatusParamProvided() throws Exception {
        var userId = UUID.randomUUID();
        when(listCollectionUseCase.list(eq(userId), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/collections")
                        .param("status", "READING")
                        .with(jwt().jwt(j -> j.subject(userId.toString()))))
                .andExpect(status().isOk());

        verify(listCollectionUseCase).list(eq(userId),
                eq(new com.comichub.collection.domain.model.CollectionFilter(CollectionStatus.READING, null)));
    }
}
