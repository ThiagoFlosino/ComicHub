package com.comichub.catalog.infrastructure.rest;

import com.comichub.catalog.domain.port.in.SearchComicByIsbnUseCase;
import com.comichub.catalog.domain.port.out.ItemRepository;
import com.comichub.catalog.infrastructure.rest.dto.CatalogItemResponse;
import com.comichub.catalog.infrastructure.rest.dto.ScanIsbnRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog/items")
public class CatalogController {

    private final SearchComicByIsbnUseCase searchComicByIsbnUseCase;
    private final ItemRepository itemRepository;

    public CatalogController(SearchComicByIsbnUseCase searchComicByIsbnUseCase,
                             ItemRepository itemRepository) {
        this.searchComicByIsbnUseCase = searchComicByIsbnUseCase;
        this.itemRepository = itemRepository;
    }

    @PostMapping("/scan")
    public ResponseEntity<CatalogItemResponse> scan(
            @RequestBody @Valid ScanIsbnRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        var item = searchComicByIsbnUseCase.execute(request.isbn());
        return ResponseEntity.status(HttpStatus.CREATED).body(CatalogItemResponse.from(item));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {

        return itemRepository.findById(id)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(CatalogItemResponse.from(item)))
                .orElse(ResponseEntity.of(ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND, "Item not found: " + id)).build());
    }

    @GetMapping
    public ResponseEntity<?> findByFilter(
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String series,
            @AuthenticationPrincipal Jwt jwt) {

        if (isbn != null) {
            return itemRepository.findByIsbn(isbn)
                    .<ResponseEntity<?>>map(item -> ResponseEntity.ok(CatalogItemResponse.from(item)))
                    .orElse(ResponseEntity.of(ProblemDetail.forStatusAndDetail(
                            HttpStatus.NOT_FOUND, "Item not found for ISBN: " + isbn)).build());
        }

        if (series != null) {
            List<CatalogItemResponse> items = itemRepository.findBySeries(series)
                    .stream().map(CatalogItemResponse::from).toList();
            return ResponseEntity.ok(items);
        }

        return ResponseEntity.badRequest().build();
    }
}
