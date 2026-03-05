package com.comichub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Task #001 – Testes de infraestrutura local.
 *
 * Verifica que:
 *   1. Container PostgreSQL sobe com sucesso.
 *   2. Container LocalStack sobe com sucesso.
 *   3. Flyway executa a migração V1 e cria a tabela 'items'.
 *
 * Requer Docker disponível. Desabilitado automaticamente sem Docker.
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class ContainersBootstrapTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void postgresContainerShouldBeRunning() {
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void localstackContainerShouldBeRunning() {
        assertThat(localstack.isRunning()).isTrue();
    }

    @Test
    void flywayV1ShouldCreateItemsTable() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name   = 'items'
                """,
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void itemsTableShouldHaveExpectedColumns() {
        var columns = jdbcTemplate.queryForList(
                """
                SELECT column_name
                FROM information_schema.columns
                WHERE table_schema = 'public'
                  AND table_name   = 'items'
                ORDER BY ordinal_position
                """,
                String.class
        );
        assertThat(columns).containsExactly(
                "id", "isbn", "title", "publisher", "series", "volume", "variant", "cover_image", "created_at",
                "author", "synopsis"
        );
    }

    @Test
    void wishlistsTableShouldExist() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name   = 'wishlists'
                """,
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void priceHistoryTableShouldExist() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name   = 'price_history'
                """,
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }
}
