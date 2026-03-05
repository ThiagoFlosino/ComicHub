package com.comichub.user.application.usecase;

import com.comichub.user.domain.model.User;
import com.comichub.user.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Task #004 – Testa o provisionamento de utilizador após login bem-sucedido.
 */
@ExtendWith(MockitoExtension.class)
class ProvisionUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProvisionUserService service;

    @Test
    void shouldReturnExistingUser_whenUserAlreadyRegistered() {
        // given
        var sub = UUID.randomUUID().toString();
        var existing = new User(UUID.fromString(sub), "user@test.com", Instant.now(), "COGNITO");
        when(userRepository.findById(UUID.fromString(sub))).thenReturn(Optional.of(existing));

        // when
        var result = service.provision(sub, "user@test.com", "COGNITO");

        // then
        assertThat(result).isEqualTo(existing);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldCreateAndReturnUser_whenUserIsNew() {
        // given
        var sub = UUID.randomUUID().toString();
        var id = UUID.fromString(sub);
        var saved = new User(id, "new@test.com", Instant.now(), "COGNITO");
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(saved);

        // when
        var result = service.provision(sub, "new@test.com", "COGNITO");

        // then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.email()).isEqualTo("new@test.com");
        assertThat(result.authProvider()).isEqualTo("COGNITO");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldPersistCorrectAuthProvider_whenProvisioningUser() {
        // given
        var sub = UUID.randomUUID().toString();
        var id = UUID.fromString(sub);
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        var result = service.provision(sub, "user@test.com", "COGNITO");

        // then
        assertThat(result.authProvider()).isEqualTo("COGNITO");
    }
}
