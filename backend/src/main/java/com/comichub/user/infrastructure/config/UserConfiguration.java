package com.comichub.user.infrastructure.config;

import com.comichub.user.application.usecase.ProvisionUserService;
import com.comichub.user.domain.port.in.ProvisionUserUseCase;
import com.comichub.user.domain.port.out.UserRepository;
import com.comichub.user.infrastructure.adapter.JpaUserRepositoryAdapter;
import com.comichub.user.infrastructure.persistence.SpringDataUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfiguration {

    @Bean
    public UserRepository userRepository(SpringDataUserRepository springDataUserRepository) {
        return new JpaUserRepositoryAdapter(springDataUserRepository);
    }

    @Bean
    public ProvisionUserUseCase provisionUserUseCase(UserRepository userRepository) {
        return new ProvisionUserService(userRepository);
    }
}
