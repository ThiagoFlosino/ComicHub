package com.comichub.user.infrastructure.security;

import com.comichub.user.domain.port.in.ProvisionUserUseCase;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

/**
 * Converte o JWT validado em um token de autenticação Spring Security.
 * Aproveita o momento para provisionar o utilizador na base de dados (upsert).
 */
public class CognitoJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final ProvisionUserUseCase provisionUserUseCase;

    public CognitoJwtConverter(ProvisionUserUseCase provisionUserUseCase) {
        this.provisionUserUseCase = provisionUserUseCase;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        provisionUserUseCase.provision(
                jwt.getSubject(),
                jwt.getClaimAsString("email"),
                "COGNITO"
        );
        return new JwtAuthenticationToken(jwt, List.of());
    }
}
