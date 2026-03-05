package com.comichub;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

/**
 * Classe base para testes de integração.
 * Sobe PostgreSQL e LocalStack uma única vez por JVM (singleton pattern).
 *
 * Quando o Docker não está disponível (sem socket), os containers ficam nulos
 * e as subclasses são responsáveis por usar serviços externos ou se desabilitar.
 */
public abstract class AbstractIntegrationTest {

    protected static final PostgreSQLContainer<?> postgres;
    protected static final LocalStackContainer localstack;

    static {
        if (isDockerAvailable()) {
            PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("comichub_test")
                    .withUsername("comichub")
                    .withPassword("comichub");
            pg.start();
            postgres = pg;

            LocalStackContainer ls = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.8"))
                    .withServices(S3, DYNAMODB, SQS, SECRETSMANAGER);
            ls.start();
            localstack = ls;
        } else {
            postgres = null;
            localstack = null;
        }
    }

    protected static boolean isDockerAvailable() {
        return new File("/var/run/docker.sock").exists();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        if (postgres != null) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
        }
    }
}
