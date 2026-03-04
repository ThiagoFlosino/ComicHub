package com.comichub.user.infrastructure.adapter;

import com.comichub.user.domain.model.User;
import com.comichub.user.domain.port.out.UserRepository;
import com.comichub.user.infrastructure.entity.UserEntity;
import com.comichub.user.infrastructure.persistence.SpringDataUserRepository;

import java.util.Optional;
import java.util.UUID;

public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;

    public JpaUserRepositoryAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springDataUserRepository.findById(id).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        var entity = new UserEntity(user.id(), user.email(), user.createdAt(), user.authProvider());
        return toDomain(springDataUserRepository.save(entity));
    }

    private User toDomain(UserEntity e) {
        return new User(e.getId(), e.getEmail(), e.getCreatedAt(), e.getAuthProvider());
    }
}
