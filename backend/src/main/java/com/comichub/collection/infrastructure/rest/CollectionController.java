package com.comichub.collection.infrastructure.rest;

import com.comichub.collection.domain.model.CollectionFilter;
import com.comichub.collection.domain.model.CollectionStatus;
import com.comichub.collection.domain.port.in.AddToCollectionUseCase;
import com.comichub.collection.domain.port.in.ListCollectionUseCase;
import com.comichub.collection.infrastructure.rest.dto.AddToCollectionRequest;
import com.comichub.collection.infrastructure.rest.dto.CollectionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/collections")
public class CollectionController {

    private final AddToCollectionUseCase addToCollectionUseCase;
    private final ListCollectionUseCase listCollectionUseCase;

    public CollectionController(AddToCollectionUseCase addToCollectionUseCase,
                                ListCollectionUseCase listCollectionUseCase) {
        this.addToCollectionUseCase = addToCollectionUseCase;
        this.listCollectionUseCase = listCollectionUseCase;
    }

    @PostMapping
    public ResponseEntity<CollectionResponse> addToCollection(
            @RequestBody @Valid AddToCollectionRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        var userId = UUID.fromString(jwt.getSubject());
        var status = request.status() != null ? request.status() : CollectionStatus.OWNED;
        var collection = addToCollectionUseCase.add(userId, request.itemId(), request.shelfLocation(), status);

        return ResponseEntity.status(HttpStatus.CREATED).body(CollectionResponse.from(collection));
    }

    @GetMapping
    public ResponseEntity<List<CollectionResponse>> listCollection(
            @RequestParam(required = false) CollectionStatus status,
            @RequestParam(required = false) String series,
            @AuthenticationPrincipal Jwt jwt) {

        var userId = UUID.fromString(jwt.getSubject());
        var filter = new CollectionFilter(status, series);
        var collections = listCollectionUseCase.list(userId, filter);

        return ResponseEntity.ok(collections.stream().map(CollectionResponse::from).toList());
    }
}
