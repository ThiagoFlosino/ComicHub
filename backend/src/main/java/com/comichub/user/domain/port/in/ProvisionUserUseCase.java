package com.comichub.user.domain.port.in;

import com.comichub.user.domain.model.User;

/**
 * Porta de ENTRADA: cria ou recupera o utilizador após validação do JWT.
 */
public interface ProvisionUserUseCase {

    /**
     * Garante que o utilizador existe na base de dados.
     * Se não existir, cria um novo registo.
     *
     * @param cognitoSub  claim "sub" do JWT (UUID do Cognito)
     * @param email       claim "email" do JWT
     * @param authProvider ex: "COGNITO"
     * @return utilizador existente ou recém-criado
     */
    User provision(String cognitoSub, String email, String authProvider);
}
