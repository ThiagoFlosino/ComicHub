package com.comichub.catalog.infrastructure.rest;

import com.comichub.catalog.domain.exception.ComicBookNotFoundException;
import com.comichub.catalog.domain.model.Item;
import com.comichub.catalog.domain.port.in.SearchComicByIsbnUseCase;
import com.comichub.catalog.domain.port.out.ItemRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
@Import(SecurityConfig.class)
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchComicByIsbnUseCase searchComicByIsbnUseCase;

    @MockitoBean
    private ItemRepository itemRepository;

    @MockitoBean
    private ProvisionUserUseCase provisionUserUseCase;

    private Item buildItem(String isbn) {
        return new Item(UUID.randomUUID(), isbn, "Watchmen", "Alan Moore", "DC Comics",
                "A brilliant narrative.", null, null, null, "covers/watch.webp", Instant.now());
    }

    // ─── POST /scan – Security ────────────────────────────────────────────────

    @Test
    void shouldReturn401_whenPostScanWithoutToken() throws Exception {
        mockMvc.perform(post("/api/v1/catalog/items/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\": \"9780930289232\"}"))
                .andExpect(status().isUnauthorized());
    }

    // ─── POST /scan – Endpoint ────────────────────────────────────────────────

    @Test
    void shouldReturn201_whenValidJwtAndIsbn() throws Exception {
        var isbn = "9780930289232";
        var item = buildItem(isbn);
        when(searchComicByIsbnUseCase.execute(isbn)).thenReturn(item);

        mockMvc.perform(post("/api/v1/catalog/items/scan")
                        .with(jwt().jwt(j -> j.subject(UUID.randomUUID().toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\": \"" + isbn + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(item.id().toString()))
                .andExpect(jsonPath("$.isbn").value(isbn))
                .andExpect(jsonPath("$.title").value("Watchmen"));
    }

    @Test
    void shouldReturn400_whenIsbnIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/catalog/items/scan")
                        .with(jwt().jwt(j -> j.subject(UUID.randomUUID().toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_whenIsbnNotFound() throws Exception {
        var isbn = "0000000000000";
        when(searchComicByIsbnUseCase.execute(isbn))
                .thenThrow(new ComicBookNotFoundException(isbn));

        mockMvc.perform(post("/api/v1/catalog/items/scan")
                        .with(jwt().jwt(j -> j.subject(UUID.randomUUID().toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\": \"" + isbn + "\"}"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /{id} ────────────────────────────────────────────────────────────

    @Test
    void shouldReturn401_whenGetByIdWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/items/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn200_whenItemFoundById() throws Exception {
        var item = buildItem("9780930289232");
        when(itemRepository.findById(item.id())).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/v1/catalog/items/" + item.id())
                        .with(jwt().jwt(j -> j.subject(UUID.randomUUID().toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.id().toString()))
                .andExpect(jsonPath("$.isbn").value("9780930289232"));
    }

    @Test
    void shouldReturn404_whenItemNotFoundById() throws Exception {
        var id = UUID.randomUUID();
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/catalog/items/" + id)
                        .with(jwt().jwt(j -> j.subject(UUID.randomUUID().toString()))))
                .andExpect(status().isNotFound());
    }

    // ─── GET ?isbn= ───────────────────────────────────────────────────────────

    @Test
    void shouldReturn200_whenSearchByIsbn() throws Exception {
        var isbn = "9780930289232";
        var item = buildItem(isbn);
        when(itemRepository.findByIsbn(isbn)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/v1/catalog/items")
                        .param("isbn", isbn)
                        .with(jwt().jwt(j -> j.subject(UUID.randomUUID().toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(isbn));
    }

    @Test
    void shouldReturn404_whenSearchByIsbnNotFound() throws Exception {
        var isbn = "0000000000000";
        when(itemRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/catalog/items")
                        .param("isbn", isbn)
                        .with(jwt().jwt(j -> j.subject(UUID.randomUUID().toString()))))
                .andExpect(status().isNotFound());
    }

    // ─── GET ?series= ─────────────────────────────────────────────────────────

    @Test
    void shouldReturn200_whenSearchBySeries() throws Exception {
        var items = List.of(buildItem("9780930289232"), buildItem("9780930289233"));
        when(itemRepository.findBySeries("Watchmen")).thenReturn(items);

        mockMvc.perform(get("/api/v1/catalog/items")
                        .param("series", "Watchmen")
                        .with(jwt().jwt(j -> j.subject(UUID.randomUUID().toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
