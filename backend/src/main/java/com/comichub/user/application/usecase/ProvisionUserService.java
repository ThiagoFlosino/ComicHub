package com.comichub.user.application.usecase;

import com.comichub.user.domain.model.User;
import com.comichub.user.domain.port.in.ProvisionUserUseCase;
import com.comichub.user.domain.port.out.UserRepository;

import java.time.Instant;
import java.util.UUID;

/**
 * Use Case: provisiona o utilizador na base de dados após login bem-sucedido.
 * REGRA: Nenhum import de Spring nesta camada.
 */
public class ProvisionUserService implements ProvisionUserUseCase {

    private final UserRepository userRepository;

    public ProvisionUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User provision(String cognitoSub, String email, String authProvider) {
        var id = UUID.fromString(cognitoSub);
        return userRepository.findById(id)
                .orElseGet(() -> userRepository.save(
                        new User(id, email, Instant.now(), authProvider)
                ));
    }
}
