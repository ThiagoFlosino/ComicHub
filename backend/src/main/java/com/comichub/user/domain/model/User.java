package com.comichub.user.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidade de domínio: utilizador autenticado.
 * REGRA: Nenhum import de Spring, JPA ou framework externo neste pacote.
 */
public record User(
        UUID id,
        String email,
        Instant createdAt,
        String authProvider
) {}
