package com.comichub.user.domain.port.out;

import com.comichub.user.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Porta de SAÍDA: persistência de utilizadores.
 */
public interface UserRepository {

    Optional<User> findById(UUID id);

    User save(User user);
}
