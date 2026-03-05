package com.comichub.catalog.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ScanIsbnRequest(@NotBlank String isbn) {}
